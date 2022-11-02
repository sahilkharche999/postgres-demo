package com.example.postgresdemo.dto;

import lombok.Data;

@Data
public class AuthToken {
    private String accessToken;
    private String refreshToken;
    private String name;

    public AuthToken(String accessToken, String refreshToken, String name) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.name = name;
    }
}
