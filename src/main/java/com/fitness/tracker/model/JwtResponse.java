package com.fitness.tracker.model;

public class JwtResponse {
    private String jwtToken;
    private String userId;

    public JwtResponse(String jwtToken, String userId) {
        this.jwtToken = jwtToken;
        this.userId = userId;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
