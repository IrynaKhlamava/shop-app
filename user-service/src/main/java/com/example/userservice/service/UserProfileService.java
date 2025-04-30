package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.OrderDto;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final OrderServiceClient orderServiceClient;
    private final UserMapper userMapper;

    public UserProfileDto getUserProfile(User user) {
        List<OrderDto> orders = orderServiceClient.getOrdersForUser(user.getId());
        return new UserProfileDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                userMapper.toAddressDtoList(user.getAddresses()),
                orders
        );
    }
}
