package com.example.sndi.dto;

import com.example.sndi.model.User;

public class LoginResponse {

    private String jwtToken;
    private User userDetails;

    public LoginResponse(String jwtToken, User userDetails) {
        this.jwtToken = jwtToken;
        this.userDetails = userDetails;
    }
}
