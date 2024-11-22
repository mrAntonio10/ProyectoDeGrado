package com.upb.toffi.rest;

import com.upb.cores.DocumentService;
import com.upb.cores.OrderDetailService;
import com.upb.models.detail.dto.CharInfoDto;
import com.upb.models.detail.dto.TotalCharInfoDto;
import com.upb.models.detail.dto.TotalCharProductInfoDto;
import com.upb.models.document.dto.SalesDocumentInfoDto;
import com.upb.models.document.dto.SalesUserDocumentDto;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.document.CreateDocumentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @GetMapping("char-info")
    public ResponseEntity<GenericResponse<List<TotalCharInfoDto>>> getCharInfoDataList(@RequestParam(value = "date", defaultValue = "#{T(java.time.LocalDateTime).now()}")
                                                                                        @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    (this.orderDetailService.charInfoDataList(auth, date)))
            );
        } catch (NullPointerException | IllegalArgumentException e) {
        log.error("Error {}, causa {}", e.getMessage(), e.getCause());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(),
                        e.getMessage()));
        } catch(NoSuchElementException e) {
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

    @GetMapping("char-product-info")
    public ResponseEntity<GenericResponse<List<TotalCharProductInfoDto>>> getCharProductInfoDataList(@RequestParam(value = "date", defaultValue = "#{T(java.time.LocalDateTime).now()}")
                                                                                       @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
                                                                                                     @RequestParam(value = "idBranchOffice", defaultValue = "") String idBranchOffice
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    (this.orderDetailService.charProductInfoDataList(idBranchOffice, date)))
            );
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(),
                            e.getMessage()));
        } catch(NoSuchElementException e) {
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
}