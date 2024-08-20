//package com.upb.toffi.rest;
//
//import com.upb.cores.AuthenticationService;
//import com.upb.toffi.rest.request.UserLoginRequest;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.springframework.http.ResponseEntity.ok;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/v1/auth")
//@RequiredArgsConstructor
//@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8084"}, allowCredentials = "true", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
//public class AuthController {
//
//    private final AuthenticationService authenticationService;
//
//    @PostMapping("/authenticate")
//    public ResponseEntity<?> register(@RequestBody UserLoginRequest user, HttpServletRequest request, HttpServletResponse response) {
//       try {
//           log.info("entrando a auth");
//           return ok(authenticationService.authenticate(user.getUsername(), user.getPassword()));
//       }   catch (UsernameNotFoundException e){
//           log.info("Error {}", e);
//
//           Map<String, Object> responseBody = new HashMap<>();
//           responseBody.put("mensaje", "Error en busqueda de usuarios");
//           return ResponseEntity.badRequest().body(responseBody);
//       }
//        catch (Exception e){
//           log.info("Error inesperado {}", e);
//
//           Map<String, Object> responseBody = new HashMap<>();
//           responseBody.put("mensaje", "Error en busqueda de usuarios");
//           return ResponseEntity.badRequest().body(responseBody);
//       }
//    }
//}
