package com.project.memegen.controller;

import com.project.memegen.entity.User;
import com.project.memegen.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestParam String username, @RequestParam String password) {
        String token = userService.loginUser(username, password);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestParam String username, @RequestParam String password) {
        String token = userService.registerUser(username,password);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Map<String, Boolean>> resetPassword(@RequestParam String username, @RequestParam String password) {
        User changedUser = userService.resetPassword(username,password);
        Map<String, Boolean> response = new HashMap<>();
        response.put("changed", changedUser != null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find-user")
    public ResponseEntity<Map<String, Boolean>> isUserExist(@RequestParam String username){
        boolean exist = userService.isUserExists(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exist);
        return ResponseEntity.ok(response);
    }
}
