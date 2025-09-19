package com.upb.cores.stereum.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthResponse implements Serializable {
    private String access_token;
}