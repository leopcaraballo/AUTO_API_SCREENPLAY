package com.sofka.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    private String identifier;
    private String password;
}
