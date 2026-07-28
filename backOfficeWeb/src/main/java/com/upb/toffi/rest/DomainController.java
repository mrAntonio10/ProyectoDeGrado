package com.upb.toffi.rest;

import com.upb.cores.DomainService;
import com.upb.toffi.config.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/domains")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
public class DomainController {

    private final DomainService domainService;

    @GetMapping("/{id-domain}/check-usage")
    public ResponseEntity<GenericResponse<Long>> checkDomainValueUsage(@PathVariable("id-domain") String idDomain) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(), domainService.checkDomainValueUsage(idDomain)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al verificar uso de dominio", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @GetMapping("/master")
    public ResponseEntity<GenericResponse<List<String>>> getMasterDomains() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(), domainService.getMasterDomains(authentication)));
        } catch (Exception e) {
            log.error("Error al obtener dominios maestros", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error en el servidor"));
        }
    }

    @GetMapping("/{domainName}")
    public ResponseEntity<GenericResponse<List<com.upb.models.utils.dto.DomainAdminDto>>> getDomainValues(@PathVariable String domainName) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(), domainService.getDomainValues(authentication, domainName)));
        } catch (Exception e) {
            log.error("Error al obtener valores del dominio", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error en el servidor"));
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponse<com.upb.models.utils.dto.DomainAdminDto>> createDomainValue(@RequestBody com.upb.toffi.rest.request.domain.CreateDomainAdminRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(), 
                domainService.createDomainValue(authentication, request.getDomain(), request.getName(), request.getDescription())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear valor de dominio", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error en el servidor"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<com.upb.models.utils.dto.DomainAdminDto>> updateDomainValue(@PathVariable String id, @RequestBody com.upb.toffi.rest.request.domain.CreateDomainAdminRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(), 
                domainService.updateDomainValue(authentication, id, request.getName(), request.getDescription())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar valor de dominio", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error en el servidor"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> deleteDomainValue(@PathVariable String id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            domainService.deleteDomainValue(authentication, id);
            return ok(GenericResponse.success(HttpStatus.OK.value(), "Valor de Dominio eliminado con éxito"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al eliminar valor de dominio", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error en el servidor"));
        }
    }
}
