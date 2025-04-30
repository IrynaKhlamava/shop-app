package com.example.authservice.service;

import com.example.authservice.dto.RegisterRequestDto;
import com.example.authservice.exception.UserNotFoundException;
import com.example.authservice.exception.ValidationException;
import com.example.authservice.model.AuthMethod;
import com.example.authservice.model.RoleName;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createGoogleUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setAuthMethod(AuthMethod.GOOGLE);
        user.setRole(RoleName.USER);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User createStandardUser(RegisterRequestDto request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(RoleName.USER);
        user.setAuthMethod(AuthMethod.STANDARD);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public RoleName getUserRole(String userId) {
        return userRepository.findById(userId)
                .map(User::getRole)
                .orElse(RoleName.USER);
    }

    public User validateUserCredentials(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ValidationException();
        }

        return user;
    }

}
