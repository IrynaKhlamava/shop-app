package com.example.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleOAuthRequest {

    @NotBlank(message = "Google ID token must not be blank")
    @JsonProperty("id_token")
    private String idToken;

}
