package com.upb.toffi.rest;

import com.upb.cores.PaymentIntegrationService;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.paymentIntegration.PaymentIntegrationCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments-integration")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class PaymentIntegrationController {
    private final PaymentIntegrationService paymentIntegrationService;

    @PostMapping
    public ResponseEntity<GenericResponse<String>> createPaymentIntegration(@RequestBody PaymentIntegrationCreateRequest pc) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                           paymentIntegrationService.createPayment(
                                   pc.getUser(), pc.getPassword(), pc.getApiKey(), pc.getIdEnterprise()
                           )
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

}