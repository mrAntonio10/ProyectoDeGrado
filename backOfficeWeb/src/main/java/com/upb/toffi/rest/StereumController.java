package com.upb.toffi.rest;

import com.upb.cores.stereum.client.StereumAuthClient;
import com.upb.cores.stereum.client.StereumCreateChargeClient;
import com.upb.cores.stereum.client.StereumVerifyTransactionClient;
import com.upb.models.user.User;
import com.upb.models.user.dto.StereumCreateChargeRequest;
import com.upb.models.user.dto.StereumTokenAuthResponseDto;
import com.upb.models.user.dto.StereumVerifyChargeRequest;
import com.upb.toffi.config.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/stereum")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class StereumController {

    private final StereumAuthClient stereumAuthClient;
    private final StereumCreateChargeClient stereumCreateChargeClient;
    private final StereumVerifyTransactionClient verifyTransactionClient;

    @GetMapping("/token")
    public ResponseEntity<GenericResponse<StereumTokenAuthResponseDto>> testToken() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            log.info("Este peji tiene values de {}", auth);

            String token = stereumAuthClient.getJwtToken(user.getId());

            return ResponseEntity.ok(GenericResponse.success(HttpStatus.OK.value(),
                    StereumTokenAuthResponseDto.builder()
                            .token(token)
                            .build()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @PostMapping("/create-charge")
    public ResponseEntity<GenericResponse<?>> createCharge(@RequestBody StereumCreateChargeRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                           stereumCreateChargeClient.createCharge(request, user)
                    )
            );
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @PostMapping("/verify-transaction")
    public ResponseEntity<GenericResponse<?>> verifyTransaction(@RequestBody StereumVerifyChargeRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();

            return ResponseEntity.ok(GenericResponse.success(HttpStatus.OK.value(),
                    verifyTransactionClient.verifyCharge(request, user))
                    );
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }
}