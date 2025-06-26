package ru.zuzex.practice.authms.dto.request;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
