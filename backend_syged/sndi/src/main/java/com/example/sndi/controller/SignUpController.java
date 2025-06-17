package com.example.sndi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.sndi.dto.SignupRequest;
import com.example.sndi.service.AuthService;

@RestController
@RequestMapping("/signup")
public class SignUpController {

    private AuthService authService;

    public SignUpController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<Boolean> signupUser(@RequestBody SignupRequest signupRequest) {
        boolean isUserCreated = authService.CreateUser(signupRequest);
        return ResponseEntity.ok(isUserCreated);
    }
}
