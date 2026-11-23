package com.upb.toffi.rest;

import com.upb.cores.ProductService;
import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductDto;
import com.upb.models.product.dto.ProductListDto;
import com.upb.models.product.dto.ProductWithImageDto;
import com.upb.models.product.dto.SyncGarmentsResultDto;
import com.upb.models.warehouse.dto.WarehousePagedDto;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.product.CreateProductRequest;
import com.upb.toffi.rest.request.product.SyncGarmentsRequest;
import com.upb.toffi.rest.request.product.UpdateProductRequest;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})

public class ProductController {
    private final ProductService productService;


    @GetMapping("")
    public ResponseEntity<GenericResponse<PagedModel<ProductListDto>>> getProductsPageable(@RequestParam(value = "filter", defaultValue = "") String filterByProductName,
                                                                                                       @RequestParam(value = "category", defaultValue = "") String category,
                                                                                                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                       @RequestParam(value = "size", defaultValue = "5") Integer pageSize,
                                                                                                       @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                                                                       @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(), new PagedModel<>(
                    (this.productService.getProductsList(auth, filterByProductName, category, pageable))))
            );
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @GetMapping("/with-images")
    public ResponseEntity<GenericResponse<PagedModel<ProductWithImageDto>>> getProductsWithImagesPageable(@RequestParam(value = "filter", defaultValue = "") String filterByProductName,
                                                                                                       @RequestParam(value = "category", defaultValue = "") String category,
                                                                                                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                       @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                                                                                                       @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                                                                       @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(), new PagedModel<>(
                    (this.productService.getProductsWithImagesList(auth, filterByProductName, category, pageable))))
            );
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @GetMapping("{id-product}")
    public ResponseEntity<GenericResponse<Product>> getProductById(@PathVariable("id-product") String idProduct
    ) {
        try {

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.productService.getProductById(idProduct))
            );
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idProduct, e.getCause());
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

    @GetMapping("/list/{category}")
    public ResponseEntity<GenericResponse<List<ProductListDto>>> getProductListByCategory(
            @PathVariable("category") String category) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.productService.getProductsListByCategory(auth, category))
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
    public ResponseEntity<GenericResponse<ProductDto>> createProduct(@RequestBody CreateProductRequest product) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    productService.createProduct(auth, product.getName(), product.getCategory(), product.getBeverageFormat(),
                            product.getSku(), product.getPhoto())
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

    @PutMapping("")
    public ResponseEntity<GenericResponse<ProductDto>> updateProduct(@RequestBody UpdateProductRequest product) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                            productService.updateProduct(product.getId(),
                                    product.getName(), product.getCategory(), product.getBeverageFormat(),
                                    product.getSku(), product.getPhoto())
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

    @DeleteMapping("/{id-product}")
    public ResponseEntity<GenericResponse<ProductDto>> deleteProduct(@PathVariable("id-product") String idProduct) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    productService.deleteProduct(idProduct)
            ));
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idProduct,e.getCause());
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

    /**
     * Rekids 1 (Público - Sin Login): Obtener prendas de las tiendas Santa Cruz / La Paz.
     * Requiere seleccionar primero la región (SANTA_CRUZ o LA_PAZ).
     */
    @GetMapping("/public/region/{region}")
    public ResponseEntity<GenericResponse<PagedModel<ProductWithImageDto>>> getProductsByRegion(
            @PathVariable("region") String region,
            @RequestParam(value = "eventId", defaultValue = "") String eventId,
            @RequestParam(value = "category", defaultValue = "") String category,
            @RequestParam(value = "filter", defaultValue = "") String filter,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "sortDir", defaultValue = "DESC") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
    ) {
        try {
            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    new PagedModel<>(this.productService.getProductsByRegion(region, eventId, category, filter, pageable))));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener prendas por región {}", region, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor al obtener las prendas de la región."));
        }
    }

    /**
     * Rekids 2 (Público - Punto de Venta Sin Login): Buscar prenda por CÓDIGO (SKU) en la región seleccionada
     * para adicionarla al detalle de venta online.
     */
    @GetMapping("/by-code/{region}/{code}")
    public ResponseEntity<GenericResponse<ProductWithImageDto>> getProductByCodeAndRegion(
            @PathVariable("region") String region,
            @PathVariable("code") String code
    ) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.productService.getProductByCodeAndRegion(code, region)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al buscar prenda por código {} en región {}", code, region, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor al buscar la prenda por código."));
        }
    }

    /**
     * Rekids 3 (Admin): SINCRONIZAR prendas desde el portal externo (FeignClient) hacia nuestro sistema
     * y registrarlas automáticamente en Novedades.
     */
    @PostMapping("/sync")
    public ResponseEntity<GenericResponse<SyncGarmentsResultDto>> syncGarmentsFromExternalPortal(
            @RequestBody(required = false) SyncGarmentsRequest req
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String region = req != null ? req.getRegion() : null;
            var garments = req != null ? req.getGarments() : null;
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.productService.syncGarmentsFromExternalPortal(auth, region, garments)));
        } catch (Exception e) {
            log.error("Error al sincronizar prendas externas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor al sincronizar prendas."));
        }
    }
}
