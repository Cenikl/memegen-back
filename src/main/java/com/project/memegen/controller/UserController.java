package com.project.memegen.controller;

import com.project.memegen.entity.User;
import com.project.memegen.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/hello")
    public String helloText(){
        return "hello world!";
    }

    @PutMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password) {
        return userService.loginUser(username,password);
    }

    @PutMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        return userService.registerUser(username,password);
    }

    @PutMapping("/reset-password")
    public User resetPassword(@RequestParam String username, @RequestParam String password) {
        return userService.resetPassword(username,password);
    }

    @GetMapping("/find-user")
    public boolean isUserExist(@RequestParam String username){
        return userService.isUserExists(username);
    }
}
