package com.example.userservice.mapper;

import com.example.userservice.dto.AddressDto;
import com.example.userservice.dto.OrderDto;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.model.Address;
import com.example.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "userId")
    UserProfileDto toUserProfileDto(User user);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orders", target = "orders")
    UserProfileDto toUserProfileDto(User user, List<OrderDto> orders);

    List<AddressDto> toAddressDtoList(List<Address> addresses);

}
