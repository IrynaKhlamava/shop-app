package com.example.authservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedFromAuthEvent {
    private String userId;
    private String firstName;
    private String lastName;
}
