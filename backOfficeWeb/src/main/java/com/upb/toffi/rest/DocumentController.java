package com.upb.toffi.rest;

import com.upb.cores.DocumentService;
import com.upb.models.document.dto.SalesDocumentInfoDto;
import com.upb.models.document.dto.SalesUserDocumentDto;
import com.upb.models.enterprise.dto.EnterpriseStateDto;
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
import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping("")
    public ResponseEntity<GenericResponse<PagedModel<SalesUserDocumentDto>>> getUserSalesPageableDocument(@RequestParam(value = "filter", defaultValue = "") String filter,
                                                                                                      @RequestParam(value = "date", defaultValue = "#{T(java.time.LocalDateTime).now()}")
                                                                                                         @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
                                                                                                      @RequestParam(value = "state", defaultValue = "") String state,
                                                                                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                      @RequestParam(value = "size", defaultValue = "5") Integer pageSize,
                                                                                                      @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                                                                      @RequestParam(value = "sortBy", defaultValue = "deliveryDate") String sortBy
                                                                         ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(), new PagedModel<>(
                    (this.documentService.getSalesUserDocumentList(authentication, filter, date, state,pageable))))
            );
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

    @GetMapping("management")
    public ResponseEntity<GenericResponse<PagedModel<SalesUserDocumentDto>>> getSalesPageableDocument(@RequestParam(value = "filter", defaultValue = "") String filter,
                                                                                                          @RequestParam(value = "date", defaultValue = "#{T(java.time.LocalDateTime).now()}")
                                                                                                             @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
                                                                                                          @RequestParam(value = "idUser", defaultValue = "") String idUser,
                                                                                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                          @RequestParam(value = "size", defaultValue = "5") Integer pageSize,
                                                                                                          @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                                                                          @RequestParam(value = "sortBy", defaultValue = "deliveryDate") String sortBy
    ) {
        try {

            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(), new PagedModel<>(
                    (this.documentService.getManagementSalesDocumentList(filter, date, idUser,pageable))))
            );
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

    @GetMapping("{id-document}")
    public ResponseEntity<GenericResponse<SalesDocumentInfoDto>> getSalesDocumentInfoByDocumentById(@PathVariable("id-document") String idDoc
    ) {
        try {

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.documentService.getSalesDocumentInfoByDocumentById(idDoc))
            );
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idDoc,e.getCause());
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
    public ResponseEntity<GenericResponse<String>> createDetailDocument(@RequestBody CreateDocumentRequest d) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    documentService.createDetailDocument(authentication, d.getDeliveryInformation(), d.getTotalPrice(),
                            d.getTotalDiscount(), d.getPaymentMethod(), d.getDetailList())
            ));
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(),
                            e.getMessage()));
        } catch (IllegalStateException e ) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(),
                            e.getMessage()));
        }
        catch (NoSuchElementException e) {
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

    @DeleteMapping("/delete/{id-doc}")
    public ResponseEntity<GenericResponse<EnterpriseStateDto>> deleteDocument(@PathVariable("id-doc") String idDoc) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    documentService.deleteDocumentById(idDoc)
            ));
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idDoc,e.getCause());
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
