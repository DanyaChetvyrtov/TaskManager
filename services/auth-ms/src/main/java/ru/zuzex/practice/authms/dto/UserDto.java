package ru.zuzex.practice.authms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.zuzex.practice.authms.model.Role;
import ru.zuzex.practice.authms.validation.OnCreate;
import ru.zuzex.practice.authms.validation.OnUpdate;
import ru.zuzex.practice.authms.validation.UniqueUsername;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull(message = "Id can't be null", groups = OnUpdate.class)
    private UUID id;

    @NotNull(message = "Username must be not null", groups = {OnCreate.class, OnUpdate.class})
    @Length(min = 2, max = 100, message = "Username should be in range between 2 and 255 chars", groups = {OnCreate.class, OnUpdate.class})
    @UniqueUsername(message = "User with such username already exists", groups = OnCreate.class)
    private String username;

    @NotNull(message = "Username must be not null", groups = OnUpdate.class)
    private Boolean isActive;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordConfirmation;
}
