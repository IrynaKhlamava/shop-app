package com.example.orderservice.mapper;

import com.example.orderservice.dto.CartItemDto;
import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItem(CartItemDto cartItemDto);

    List<OrderItem> toOrderItems(List<CartItemDto> cartItems);

}
