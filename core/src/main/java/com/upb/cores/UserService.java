package com.upb.cores;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface UserService {
    HashMap<String, Object> findByUsernameAndPassword(String nombreUsuario, String password);

    String findUserByEmailAndStateActive(String email);

}
