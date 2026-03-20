package com.sofka.models;

public class LoginRequest {
    private String identifier;
    private String password;

    public LoginRequest() {}

    public LoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public static LoginRequestBuilder builder() { return new LoginRequestBuilder(); }

    public static class LoginRequestBuilder {
        private String identifier;
        private String password;
        public LoginRequestBuilder identifier(String identifier) { this.identifier = identifier; return this; }
        public LoginRequestBuilder password(String password) { this.password = password; return this; }
        public LoginRequest build() { return new LoginRequest(identifier, password); }
    }
}
