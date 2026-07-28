package com.upb.toffi.rest;

import com.upb.cores.SupplierService;
import com.upb.models.supplier.Supplier;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.supplier.CreateSupplierRequest;
import com.upb.toffi.rest.request.supplier.UpdateSupplierRequest;
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
@RequestMapping("/api/v1/supplier")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping("")
    public ResponseEntity<GenericResponse<List<Supplier>>> getAllSuppliers() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(), supplierService.getAllSuppliers(auth)));
        } catch (Exception e) {
            log.error("Error al obtener proveedores", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @PostMapping("")
    public ResponseEntity<GenericResponse<Supplier>> createSupplier(@RequestBody CreateSupplierRequest req) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    supplierService.createSupplier(req.getName(), req.getContactName(), req.getPhone(), auth)));
        } catch (IllegalArgumentException e) {
            log.error("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GenericResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear proveedor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<Supplier>> updateSupplier(@PathVariable("id") String id,
                                                                     @RequestBody UpdateSupplierRequest req) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    supplierService.updateSupplier(id, req.getName(), req.getContactName(), req.getPhone(), req.getState())));
        } catch (NoSuchElementException e) {
            log.error("Proveedor no encontrado ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar proveedor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Supplier>> deleteSupplier(@PathVariable("id") String id) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(), supplierService.deleteSupplier(id)));
        } catch (NoSuchElementException e) {
            log.error("Proveedor no encontrado ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al eliminar proveedor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }
}
