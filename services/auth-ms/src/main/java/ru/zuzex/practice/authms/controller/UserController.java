package ru.zuzex.practice.authms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zuzex.practice.authms.mapper.UserMapper;
import ru.zuzex.practice.authms.service.AuthService;
import ru.zuzex.practice.authms.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PutMapping("/{userId}")
    public ResponseEntity<HttpStatus> makeAdmin(@PathVariable UUID userId) {
        userService.makeAdmin(userId);
        return ResponseEntity.noContent().build();
    }
}
