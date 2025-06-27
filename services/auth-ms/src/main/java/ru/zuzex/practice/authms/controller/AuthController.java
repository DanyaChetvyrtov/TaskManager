package ru.zuzex.practice.authms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zuzex.practice.authms.dto.UserDto;
import ru.zuzex.practice.authms.dto.request.JwtRequest;
import ru.zuzex.practice.authms.dto.response.JwtResponse;
import ru.zuzex.practice.authms.mapper.UserMapper;
import ru.zuzex.practice.authms.model.User;
import ru.zuzex.practice.authms.service.AuthService;
import ru.zuzex.practice.authms.service.UserService;
import ru.zuzex.practice.authms.validation.OnCreate;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody @Validated JwtRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Validated(OnCreate.class) UserDto userDto) {
        System.out.println(userDto);
        var user = userMapper.toEntity(userDto);
        System.out.println(userDto);
        userDto = userMapper.toDto(userService.create(user));
        System.out.println(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
