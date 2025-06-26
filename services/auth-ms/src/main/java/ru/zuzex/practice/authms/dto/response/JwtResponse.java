package ru.zuzex.practice.authms.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class JwtResponse {
    private UUID id;
    private String username;
    private String accessToken;
    private String refreshToken;
}
