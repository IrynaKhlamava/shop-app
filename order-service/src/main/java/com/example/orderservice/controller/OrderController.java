package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.exception.AccessDeniedException;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}/place")
    public ResponseEntity<OrderDto> placeOrder(
            @PathVariable String userId,
            @RequestParam String shippingAddress) {
        return ResponseEntity.ok(orderService.placeOrder(userId, shippingAddress));
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<OrderDto>> getOrderHistory(
            @PathVariable String userId) {//,
//            @RequestHeader("X-User-Id") String requesterId) {
//        if (!userId.equals(requesterId)) {
//            throw new AccessDeniedException();
//        }
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }


    @PostMapping("/{orderId}/return")
    public ResponseEntity<OrderDto> returnItem(
            @PathVariable String orderId,
            @RequestParam String productId
    ) {
        return ResponseEntity.ok(orderService.returnItem(orderId, productId));
    }

}
