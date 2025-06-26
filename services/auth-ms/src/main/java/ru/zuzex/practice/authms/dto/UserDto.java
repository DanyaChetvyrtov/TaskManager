package ru.zuzex.practice.authms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zuzex.practice.authms.model.Role;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    private int age;
    private String email;
    private Set<Role> roles;

    private String password;
    private String passwordConfirmation;
}
