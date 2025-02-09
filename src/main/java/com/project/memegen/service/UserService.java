package com.project.memegen.service;

import com.project.memegen.entity.User;
import com.project.memegen.repository.UserRepository;
import com.project.memegen.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public String registerUser(String username, String password) {
        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .createdAt(Timestamp.from(Instant.now()))
                .build();
        userRepository.save(newUser);
        return loginUser(username,password);
    }

    public String loginUser(String username,String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return JwtUtil.generateToken(username);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Username or password are incorrect");
    }

    public boolean isUserExists(String username){
        return userRepository.findByUsername(username).isPresent();
    }

    public User resetPassword(String username, String password) {
        User oldUser = userRepository.findByUsername(username).get();
        oldUser.setPassword(passwordEncoder.encode(password));
        return userRepository.save(oldUser);
    }

}
