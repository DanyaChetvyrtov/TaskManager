package ru.zuzex.practice.authms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zuzex.practice.authms.exception.exception.UserNotFoundException;
import ru.zuzex.practice.authms.model.Role;
import ru.zuzex.practice.authms.model.User;
import ru.zuzex.practice.authms.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }


    @Transactional
    public User create(User user) {
        user.setRoles(Set.of(new Role(1, "ROLE_USER")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User update(User user) {
        var userDB = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("User with such id does not exist"));

        userDB.setUsername(user.getUsername());
        userDB.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(userDB);
    }

    @Transactional
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
