package com.upb.toffi.rest;

import com.upb.models.user.dto.UserAuthenticationResponse;
import com.upb.toffi.config.JwtService;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.UserLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;


@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8084"}, allowCredentials = "true", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/authenticate")
    public ResponseEntity<GenericResponse<UserAuthenticationResponse>> register(@RequestBody UserLoginRequest user, HttpServletRequest request, HttpServletResponse response) {
       try {
           log.info("Intento de conexion usuario: {}", user.getEmail());

           Authentication auth = authenticationManager
                   .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

           SecurityContextHolder.getContext().setAuthentication(auth);

           UserDetails userDetails = (UserDetails) auth.getPrincipal();
           var jwt = jwtService.generateToken(userDetails);

        return ok(GenericResponse.success(HttpStatus.OK.value(),
                UserAuthenticationResponse.builder()
                .token(jwt)
                .build()));
       }   catch (BadCredentialsException e){
           log.info("Error {}", e.getMessage());
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                   .body(GenericResponse.error(HttpStatus.UNAUTHORIZED.value(),
                   "Credenciales no válidas. Por favor, ingrese nuevamente."));
       }
        catch (Exception e){
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error en el servidor. Favor contactarse con el administrador."));
       }
    }

}
