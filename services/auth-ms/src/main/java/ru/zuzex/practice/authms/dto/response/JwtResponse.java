package ru.zuzex.practice.authms.dto.response;

import lombok.Data;

@Data
public class JwtResponse {
    private Integer id;
    private String username;
    private String accessToken;
    private String refreshToken;
}
