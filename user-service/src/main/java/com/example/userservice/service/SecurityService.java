package com.example.userservice.service;

import com.example.userservice.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    public void checkIsAdmin(String userRole) {
        if (!ROLE_ADMIN.equals(userRole)) {
            throw new AccessDeniedException();
        }
    }

    public void checkUserOrAdmin(String authenticatedUserId, String requestedUserId, String userRole) {
        boolean isAdmin = ROLE_ADMIN.equals(userRole);
        if (!isAdmin && !authenticatedUserId.equals(requestedUserId)) {
            throw new AccessDeniedException();
        }
    }
}