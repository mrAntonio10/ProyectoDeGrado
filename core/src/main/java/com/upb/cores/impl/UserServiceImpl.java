package com.upb.cores.impl;


import com.upb.repositories.UserRepository;
import com.upb.cores.UserService;
import com.upb.models.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder(10);
    }

    @Override
    public HashMap<String, Object> findByUsernameAndPassword(String email, String password) {
        Optional<User> usuarioOpt = userRepository.findByEmailAndStateActive(email);

        log.info("[{}]", usuarioOpt);
        if(!usuarioOpt.isPresent()){
            throw new NoSuchElementException("Las credenciales no pertenecen a un usuario dentro del sistema");
        }

        if(!encoder.matches(password, usuarioOpt.get().getPassword())) {
            throw new NoSuchElementException("Las credenciales no pertenecen a un usuario dentro del sistema");
        }
        HashMap<String, Object> resp =  new HashMap<>();
            resp.put("username",usuarioOpt.get().getUsername());
            resp.put("rol",usuarioOpt.get().getRol().getName());

        return resp;
    }

    @Override
    public String findUserByEmailAndStateActive(String emailrequest) {
        User userOpt =  userRepository.findByEmailAndStateActive(emailrequest)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

       return userOpt.getEmail();
    }
}
