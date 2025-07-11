package com.example.sndi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sndi.dto.SignupRequest;

public interface AuthService {

    boolean CreateUser(SignupRequest signupRequest);
}
