package ru.zuzex.practice.authms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.zuzex.practice.authms.dto.request.JwtRequest;
import ru.zuzex.practice.authms.dto.response.JwtResponse;
import ru.zuzex.practice.authms.security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtResponse login(JwtRequest loginRequest) {
        var jwtResponse = new JwtResponse();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        var user = userService.getUser(loginRequest.getUsername());

        jwtResponse.setId(user.getId());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(
                jwtTokenProvider.createAccessToken(user.getId(), user.getUsername())
        );
        jwtResponse.setRefreshToken(
                jwtTokenProvider.createRefreshToken(user.getId(), user.getUsername())
        );

        return jwtResponse;
    }

    public JwtResponse refresh(String refreshToken) {
        return jwtTokenProvider.refreshUserTokens(refreshToken);
    }
}
