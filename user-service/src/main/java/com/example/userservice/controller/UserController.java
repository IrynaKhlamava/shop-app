package com.example.userservice.controller;

import com.example.userservice.dto.AddressDto;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @PathVariable String userId,
            @RequestHeader("X-User-Id") String authenticatedUserId,
            @RequestHeader("X-User-Role") String userRole) {

        return ResponseEntity.ok(userService.getUserProfile(userId, authenticatedUserId, userRole));
    }


    @PutMapping("/{userId}/shipping-address")
    public ResponseEntity<UserProfileDto> updateShippingAddress(
            @PathVariable String userId,
            @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(userService.updateShippingAddress(userId, addressDto));
    }

    @GetMapping
    public ResponseEntity<List<UserProfileDto>> getUsers(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestHeader("X-User-Role") String userRole) {

        return ResponseEntity.ok(userService.getPagedUsers(query, page, size, userRole));
    }

}
