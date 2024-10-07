package com.upb.toffi.rest;

import com.upb.cores.EnterpriseService;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.enterprise.dto.EnterpriseDto;
import com.upb.models.enterprise.dto.EnterpriseStateDto;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.enterprise.CreateEnterpriseRequest;
import com.upb.toffi.rest.request.enterprise.UpdateEnterpriseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
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
@RequestMapping("/api/v1/enterprises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})

public class EnterpriseController {
    private final EnterpriseService enterpriseService;

    @GetMapping("")
    public ResponseEntity<GenericResponse<PagedModel<EnterpriseDto>>> getEnterprisePageable(@RequestParam(value = "filter", defaultValue = "") String filterByName,
                                                                                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                            @RequestParam(value = "size", defaultValue = "5") Integer pageSize,
                                                                                            @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                                                            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
                                                                         ) {
        try {
            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(), new PagedModel<>(
                    (this.enterpriseService.getEnterprisePageable(filterByName, pageable))))
            );
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @GetMapping("{id-enterprise}")
    public ResponseEntity<GenericResponse<Enterprise>> getEnterpriseById(@PathVariable("id-enterprise") String idEnterprise
    ) {
        try {

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.enterpriseService.getEnterpriseById(idEnterprise))
            );
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idEnterprise,e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<GenericResponse<List<EnterpriseStateDto>>> getEnterpriseList() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.enterpriseService.getEnterpriseCombo(authentication))
            );
        } catch (NoSuchElementException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @PostMapping()
    public ResponseEntity<GenericResponse<EnterpriseDto>> createEnterprise(@RequestBody CreateEnterpriseRequest enterprise) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    enterpriseService.createEnterprise(enterprise.getName(), enterprise.getEmail(), enterprise.getPhoneNumber(),
                    enterprise.getLogo(), enterprise.getDescription()))
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

    @PutMapping("")
    public ResponseEntity<GenericResponse<EnterpriseDto>> updateEnterprise(@RequestBody UpdateEnterpriseRequest enterprise) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    enterpriseService.updateEnterprise(enterprise.getId(), enterprise.getName(), enterprise.getEmail(), enterprise.getPhoneNumber(),
                            enterprise.getLogo(), enterprise.getDescription(), enterprise.getState()))
            );
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), enterprise.getId(),e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
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

    @DeleteMapping("/{id-enterprise}")
    public ResponseEntity<GenericResponse<EnterpriseStateDto>> deleteEnterprise(@PathVariable("id-enterprise") String idEnterprise) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    enterpriseService.deleteEnterpriseById(idEnterprise)
            ));
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idEnterprise,e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }
}
