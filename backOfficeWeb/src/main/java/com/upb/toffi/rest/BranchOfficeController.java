package com.upb.toffi.rest;

import com.upb.cores.BranchOfficeService;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.branchOffice.dto.BranchOfficeDto;
import com.upb.models.branchOffice.dto.BranchOfficeStateDto;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.enterprise.dto.EnterpriseDto;
import com.upb.models.enterprise.dto.EnterpriseStateDto;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.branchOffice.CreateBranchOfficeRequest;
import com.upb.toffi.rest.request.branchOffice.UpdateBranchOfficeRequest;
import com.upb.toffi.rest.request.enterprise.CreateEnterpriseRequest;
import com.upb.toffi.rest.request.enterprise.UpdateEnterpriseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/branch-offices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})

public class BranchOfficeController {
    private final BranchOfficeService branchOfficeService;

    @GetMapping("")
    public ResponseEntity<GenericResponse<PagedModel<BranchOfficeDto>>> getBranchOfficePageable(@RequestParam(value = "filter", defaultValue = "") String filterByName,
                                                                                                @RequestParam(value = "idEnterprise", defaultValue = "") String idEnterpsie,
                                                                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                @RequestParam(value = "size", defaultValue = "5") Integer pageSize,
                                                                                                @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                                                                @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
                                                                         ) {
        try {
            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            return ok(GenericResponse.success(HttpStatus.OK.value(), new PagedModel<>(
                    (this.branchOfficeService.getBranchOfficePageable(filterByName, idEnterpsie, authentication, pageable))))
            );
        } catch (NullPointerException e) {
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

    @GetMapping("{id-branch-office}")
    public ResponseEntity<GenericResponse<BranchOffice>> getBranchOfficeById(@PathVariable("id-branch-office") String idBranchOffice
    ) {
        try {

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.branchOfficeService.getBranchOfficeById(idBranchOffice))
            );
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idBranchOffice, e.getCause());
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

    @GetMapping("/list/{id-enterprise}")
    public ResponseEntity<GenericResponse<List<BranchOfficeStateDto>>> getBranchOfficeListByIdEnterprise(@PathVariable("id-enterprise") String idEnterprise
    ) {
        try {

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.branchOfficeService.getBranchOfficeListByIdEnterprise(idEnterprise))
            );
        } catch (NullPointerException e) {
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

    @PostMapping()
    public ResponseEntity<GenericResponse<BranchOfficeDto>> createBranchOffice(@RequestBody CreateBranchOfficeRequest bo) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    branchOfficeService.createBranchOffice(bo.getName(), bo.getLocation(), bo.getPhoneNumber(),
                    bo.getIdEnterprise(), bo.getInvoice(), bo.getInCode()))
            );
        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (NullPointerException e) {
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

    @PutMapping()
    public ResponseEntity<GenericResponse<BranchOfficeDto>> updateBranchOffice(@RequestBody UpdateBranchOfficeRequest bo) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    branchOfficeService.updateBranchOffice(bo.getId(), bo.getName(), bo.getLocation(), bo.getPhoneNumber(),
                            bo.getState(), bo.getInvoice(), bo.getInCode(), bo.getIdEnterprise()))
            );
        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), bo.getId(),e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (NullPointerException e) {
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

    @DeleteMapping("/{id-branch-office}")
    public ResponseEntity<GenericResponse<BranchOfficeStateDto>> deleteBranchOffice(@PathVariable("id-branch-office") String idEnterprise) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    branchOfficeService.deleteBranchOfficeById(idEnterprise)
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
