package ru.zuzex.practice.authms.security.jwt;

import ru.zuzex.practice.authms.model.User;

public class JwtEntityFactory {


    public static JwtEntity create(User user) {
        return new JwtEntity(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRoles()
        );
    }
}
