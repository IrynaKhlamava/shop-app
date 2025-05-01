package com.example.authservice.service;

import com.example.authservice.dto.AuthResponseDto;
import com.example.authservice.dto.GoogleOAuthRequest;
import com.example.authservice.dto.GoogleUserInfo;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequestDto;
import com.example.authservice.exception.AppMessages;
import com.example.authservice.exception.GoogleAuthenticationException;
import com.example.authservice.model.User;
import com.example.authservice.exception.CustomException;
import com.example.authservice.exception.EmailAlreadyExistsException;
import com.example.authservice.model.RoleName;
import com.example.authservice.kafka.UserCreatedProducer;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import static com.example.authservice.exception.AppMessages.BEARER_PREFIX;
import static com.example.authservice.exception.AppMessages.GOOGLE_EMAIL_NOT_VERIFIED;
import static com.example.authservice.exception.AppMessages.GOOGLE_TOKEN_INVALID;
import static com.example.authservice.exception.AppMessages.GOOGLE_TOKEN_MISSING;
import static com.example.authservice.exception.AppMessages.GOOGLE_TOKEN_VERIFICATION_FAILED;
import static com.example.authservice.exception.AppMessages.INVALID_OR_EXPIRED_TOKEN;
import static com.example.authservice.exception.AppMessages.LOGOUT_SUCCESS;
import static com.example.authservice.exception.AppMessages.NO_TOKEN_PROVIDED;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    private final RedisTokenService redisTokenService;

    private final UserService userService;

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    private final UserCreatedProducer userCreatedProducer;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthResponseDto register(RegisterRequestDto request) {
        try {
            User newUser = userService.createStandardUser(request);
            logger.info("New user registered: {}", newUser.getEmail());

            sendUserCreatedEvent(newUser.getId(), request.getFirstName(), request.getLastName());

            return createAuthResponse(newUser);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Attempt to register with existing email: {}", request.getEmail());
            throw new EmailAlreadyExistsException();
        }
    }

    public AuthResponseDto login(LoginRequest request) {
        User user = userService.validateUserCredentials(request.getEmail(), request.getPassword());

        return createAuthResponse(user);
    }

    public AuthResponseDto refreshAccessToken(String refreshToken) {
        String extractedRefreshToken = extractToken(refreshToken);
        String userId = validateRefreshToken(extractedRefreshToken);
        String userEmail = jwtService.extractEmail(extractedRefreshToken);

        RoleName role = userService.getUserRole(userId);
        String newAccessToken = jwtService.generateAccessToken(userId, userEmail, role );

        return new AuthResponseDto(newAccessToken, extractedRefreshToken, userId);
    }


    private String validateRefreshToken(String refreshToken) {
        if (redisTokenService.isTokenBlacklisted(refreshToken)) {
            throw new CustomException(AppMessages.REFRESH_TOKEN_BLACKLISTED, HttpStatus.UNAUTHORIZED);
        }

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new CustomException(AppMessages.REFRESH_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        }

        return jwtService.extractUserId(refreshToken);
    }

    public String extractToken(String header) {
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(7);
        }
        return header;
    }

    public AuthResponseDto createAuthResponse(User user) {
        String userId = user.getId();
        RoleName userRole = user.getRole();
        String userEmail = user.getEmail();

        String accessToken = jwtService.generateAccessToken(userId, userEmail, userRole);
        String refreshToken = jwtService.generateRefreshToken(userId, userEmail, userRole);

        return new AuthResponseDto(accessToken, refreshToken, user.getId());
    }

    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {
                long expiration = jwtService.getExpirationTime(token);
                redisTokenService.blacklistToken(token, expiration);

                logger.info("Access token blacklisted");
                return LOGOUT_SUCCESS;
            } else {
                logger.warn("Logout called with invalid token");
                return INVALID_OR_EXPIRED_TOKEN;
            }
        } else {
            logger.warn("No Authorization header provided on logout");
            return NO_TOKEN_PROVIDED;
        }
    }

    public AuthResponseDto googleLogin(GoogleOAuthRequest googleOAuthRequest) {
        String idTokenString = googleOAuthRequest.getIdToken();

        GoogleIdToken.Payload payload = verifyGoogleToken(idTokenString);
        GoogleUserInfo googleUserInfo = extractUserInfoFromGoogleToken(payload);

        User user = findOrCreateGoogleUser(googleUserInfo.getEmail());

        sendUserCreatedEvent(user.getId(), googleUserInfo.getFirstName(), googleUserInfo.getLastName());

        return createAuthResponse(user);
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            if (idTokenString == null || idTokenString.isBlank()) {
                logger.warn(GOOGLE_TOKEN_MISSING);
                throw new GoogleAuthenticationException(GOOGLE_TOKEN_MISSING);
            }

            GoogleIdToken idToken = googleIdTokenVerifier.verify(idTokenString);
            if (idToken == null) {
                logger.warn(GOOGLE_TOKEN_INVALID);
                throw new GoogleAuthenticationException(GOOGLE_TOKEN_INVALID);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                logger.warn("{}: {}", GOOGLE_EMAIL_NOT_VERIFIED, payload.getEmail());
                throw new GoogleAuthenticationException(GOOGLE_EMAIL_NOT_VERIFIED);
            }

            return payload;

        } catch (Exception e) {
            logger.error(GOOGLE_TOKEN_VERIFICATION_FAILED, e);
            throw new GoogleAuthenticationException(GOOGLE_TOKEN_VERIFICATION_FAILED, e);
        }
    }

    private User findOrCreateGoogleUser(String email) {
        return userService.findByEmail(email)
                .orElseGet(() -> userService.createGoogleUser(email));
    }

    private GoogleUserInfo extractUserInfoFromGoogleToken(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        return new GoogleUserInfo(email, firstName, lastName);
    }

    private void sendUserCreatedEvent(String userId, String firstName, String lastName) {
        try {
            userCreatedProducer.publishUserCreatedEvent(userId, firstName, lastName);
        } catch (Exception e) {
            logger.error("Failed to publish user created event for user {}", userId, e);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        String extracted = extractToken(token);
        if (!jwtService.validateToken(extracted)) {
            throw new CustomException(AppMessages.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
        return redisTokenService.isTokenBlacklisted(extracted);
    }

}