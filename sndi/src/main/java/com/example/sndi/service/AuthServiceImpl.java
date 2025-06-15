package com.example.sndi.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.sndi.dto.SignupRequest;
import com.example.sndi.model.User;
import com.example.sndi.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean CreateUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return false;
        }

        User user = new User();
        BeanUtils.copyProperties(signupRequest, user);

        String hasPassword = passwordEncoder.encode(signupRequest.getPassword());
        user.setPassword(hasPassword);
        userRepository.save(user);

        return true;
    }

}
