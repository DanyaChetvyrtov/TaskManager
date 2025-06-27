package ru.zuzex.practice.authms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zuzex.practice.authms.exception.exception.PasswordsDoNotMatchException;
import ru.zuzex.practice.authms.exception.exception.RoleNotFoundException;
import ru.zuzex.practice.authms.exception.exception.UserNotFoundException;
import ru.zuzex.practice.authms.model.User;
import ru.zuzex.practice.authms.repository.RoleRepository;
import ru.zuzex.practice.authms.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
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
    public User create(User user, String passwordConfirmation) {
        if (!user.getPassword().equals(passwordConfirmation)) throw new PasswordsDoNotMatchException();

        var userRole = roleRepository.findByName("ROLE_USER").orElseThrow(RoleNotFoundException::new);
        user.setRoles(Set.of(userRole));
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

//    @Transactional
//    public User update(User user) {
//        var userDB = userRepository.findById(user.getId())
//                .orElseThrow(() -> new UserNotFoundException("User with such id does not exist"));
//
//        userDB.setUsername(user.getUsername());
//        userDB.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        return userRepository.save(userDB);
//    }

    @Transactional
    public void updateLastLogin(UUID userId){
        var user = getUser(userId);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
//
//    @Transactional
//    public void delete(UUID id) {
//        userRepository.deleteById(id);
//    }

    @Transactional
    public void makeAdmin(UUID userId) {
        var user = getUser(userId);
        var adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(RoleNotFoundException::new);

        user.getRoles().add(adminRole);

        userRepository.save(user);
    }
}
