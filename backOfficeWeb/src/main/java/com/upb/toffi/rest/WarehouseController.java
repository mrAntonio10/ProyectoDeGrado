package com.upb.toffi.rest;

import com.upb.cores.WarehouseService;
import com.upb.models.warehouse.Warehouse;
import com.upb.models.warehouse.dto.WarehouseDto;
import com.upb.models.warehouse.dto.WarehousePageableProductsDto;
import com.upb.models.warehouse.dto.WarehousePagedDto;
import com.upb.models.warehouse.dto.WarehouseStateDto;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.warehouse.CreateWarehouseRequest;
import com.upb.toffi.rest.request.warehouse.UpdateWarehouseRequest;
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

import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping("")
    public ResponseEntity<GenericResponse<PagedModel<WarehousePagedDto>>> getWarehouseProductsPageable(@RequestParam(value = "filter", defaultValue = "") String filterByProductName,
                                                                                                       @RequestParam(value = "idBranchOffice", defaultValue = "") String idBranchOffice,
                                                                                                       @RequestParam(value = "category", defaultValue = "") String category,
                                                                                                       @RequestParam(value = "limit", defaultValue = "") String maxOrMinLimit,
                                                                                                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                       @RequestParam(value = "size", defaultValue = "5") Integer pageSize,
                                                                                                       @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                                                                       @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
                                                                         ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(), new PagedModel<>(
                    (this.warehouseService.getWarehouseProductsList(authentication, filterByProductName, idBranchOffice, category, maxOrMinLimit, pageable))))
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

    @GetMapping("/warehouse-products")
    public ResponseEntity<GenericResponse<PagedModel<WarehousePageableProductsDto>>> getWarehousePageableProducts(@RequestParam(value = "filter", defaultValue = "") String filterByProductNameOrCode,
                                                                                                                  @RequestParam(value = "category", defaultValue = "") String category,
                                                                                                                  @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                                  @RequestParam(value = "size", defaultValue = "5") Integer pageSize,
                                                                                                                  @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                                                                                  @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(), new PagedModel<>(
                    (this.warehouseService.getWarehousePageableProductsForDetail(authentication, filterByProductNameOrCode, category, pageable))))
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

    @GetMapping("{id-warehouse}")
    public ResponseEntity<GenericResponse<Warehouse>> getWarehouseById(@PathVariable("id-warehouse") String idWarehouse
    ) {
        try {

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.warehouseService.getWarehouseById(idWarehouse))
            );
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idWarehouse,e.getCause());
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

    @GetMapping("{id-branchOffice}/{product-name}/{beverage-format}")
    public ResponseEntity<GenericResponse<Warehouse>> getWarehouseByIdBranchOfficeAndProductName(@PathVariable("id-branchOffice") String idBranchOffice,
                                                                        @PathVariable("product-name") String productName,
                                                                        @PathVariable("beverage-format") String beverageFormat
    ) {
        try {

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.warehouseService.getWarehouseByIdBranchOfficeProductNameAndBeverageFormat(idBranchOffice, productName, beverageFormat))
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
    public ResponseEntity<GenericResponse<WarehouseDto>> createWarehouse(@RequestBody CreateWarehouseRequest w) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    warehouseService.createWarehouse(w.getIdProduct(), w.getIdBranchOffice(), w.getStock(),
                            w.getUnitaryCost(), w.getMaxProduct(), w.getMinProduct()))
            );
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(),
                            e.getMessage()));
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

    @PutMapping("")
    public ResponseEntity<GenericResponse<WarehouseDto>> updateWarehouse(@RequestBody UpdateWarehouseRequest w) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    warehouseService.updateWarehouse(w.getId(), w.getIdProduct(), w.getIdBranchOffice(), w.getStock(),
                            w.getUnitaryCost(), w.getMaxProduct(), w.getMinProduct(), w.getSku()))
            );
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), w.getId(),e.getCause());
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

    @DeleteMapping("/{id-warehouse}")
    public ResponseEntity<GenericResponse<WarehouseStateDto>> deleteWarehouse(@PathVariable("id-warehouse") String idWarehouse) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    warehouseService.deleteWarehouseById(idWarehouse)
            ));
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idWarehouse,e.getCause());
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
