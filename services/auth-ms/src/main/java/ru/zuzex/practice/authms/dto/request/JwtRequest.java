package ru.zuzex.practice.authms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JwtRequest {
    @NotNull(message = "Username must be not null")
    private String username;
    @NotNull(message = "Password must be not null")
    private String password;
}
