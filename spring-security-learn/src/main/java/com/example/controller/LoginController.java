package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login success";
    }

    @GetMapping("/teacher/page")
    public String teacher() {
        return "teacher page";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }
}
