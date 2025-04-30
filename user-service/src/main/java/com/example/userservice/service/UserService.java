package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.AddressDto;
import com.example.userservice.dto.OrderDto;
import com.example.userservice.event.UserCreatedFromAuthEvent;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.exception.AccessDeniedException;
import com.example.userservice.exception.CustomException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.Address;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserProfileService userProfileService;
    private final SecurityService securityService;
    private final OrderServiceClient orderServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserProfileDto getUserProfile(String userId, String authenticatedUserId, String userRole) {
        securityService.checkUserOrAdmin(authenticatedUserId, userId, userRole);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<OrderDto> orders = orderServiceClient.getOrdersForUser(userId);

        return userMapper.toUserProfileDto(user, orders);
    }


    public UserProfileDto updateShippingAddress(String userId, AddressDto addressDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setZipCode(addressDto.getZipCode());
        address.setCountry(addressDto.getCountry());
        address.setUser(user);

        user.getAddresses().add(address);

        userRepository.save(user);
        return userProfileService.getUserProfile(user);
    }

    public List<UserProfileDto> getPagedUsers(String query, int page, int size, String userRole) {
        securityService.checkIsAdmin(userRole);

        Pageable pageable = PageRequest.of(page, size);

        Page<User> result;
        if (query == null || query.isBlank()) {
            result = userRepository.findAll(pageable);
        } else {
            result = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query, pageable);
        }

        return result
                .stream()
                .map(userMapper::toUserProfileDto)
                .collect(Collectors.toList());
    }


    public void createUserFromEvent(UserCreatedFromAuthEvent event) {
        User user = new User();
        user.setId(event.getUserId());
        user.setFirstName(event.getFirstName());
        user.setLastName(event.getLastName());
        userRepository.save(user);
        logger.info("Created user profile from Kafka event: {}", event.getUserId());
    }
}
