package com.upb.toffi.rest;

import com.upb.cores.ProductService;
import com.upb.models.product.Product;
import com.upb.models.product.dto.*;
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
     * Rekids 1 (Público - Sin Login): Obtener prendas paginadas y filtradas de las tiendas Santa Cruz / La Paz (o TODOS).
     */
    @GetMapping("/public/region/{region}")
    public ResponseEntity<GenericResponse<PagedModel<ProductWithImageDto>>> getProductsByRegion(
            @PathVariable("region") String region,
            @RequestParam(value = "branchId", defaultValue = "") String branchId,
            @RequestParam(value = "eventId", defaultValue = "") String eventId,
            @RequestParam(value = "category", defaultValue = "") String category,
            @RequestParam(value = "clothingSize", defaultValue = "") String clothingSize,
            @RequestParam(value = "brand", defaultValue = "") String brand,
            @RequestParam(value = "condition", defaultValue = "") String condition,
            @RequestParam(value = "availability", defaultValue = "") String availability,
            @RequestParam(value = "filter", defaultValue = "") String filter,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "6") Integer pageSize,
            @RequestParam(value = "sortDir", defaultValue = "DESC") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
    ) {
        try {
            int safePage = (page != null && page >= 0) ? page : 0;
            int safeSize = (pageSize != null && pageSize >= 1 && pageSize <= 100) ? pageSize : 6;
            PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.Direction.fromString(sortDir), sortBy);
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    new PagedModel<>(this.productService.getProductsByRegionFiltered(
                            region,
                            branchId,
                            eventId,
                            category,
                            clothingSize,
                            brand,
                            condition,
                            availability,
                            filter,
                            pageable
                    ))));
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

    /**
     * ReKids Sandbox 4: Listar las sucursales autorizadas desde el FeignClient (/integrations/v1/branches).
     */
    @GetMapping({"/public/rekids/branches", "/rekids/branches"})
    public ResponseEntity<GenericResponse<List<RekidsSandboxBranchDto>>> getRekidsBranches() {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(), this.productService.getRekidsBranches()));
        } catch (Exception e) {
            log.error("Error al obtener sucursales ReKids Sandbox", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error al consultar sucursales de ReKids Sandbox."));
        }
    }

    /**
     * ReKids Sandbox 5: Consultar productos de una sucursal específica con soporte de cursor, paginación y filtros
     * (/integrations/v1/products?branch_id=UUID&cursor=...&limit=...).
     */
    @GetMapping({"/public/rekids/branches/{branchId}/products", "/rekids/branches/{branchId}/products"})
    public ResponseEntity<GenericResponse<RekidsSandboxProductPageResponseDto>> getRekidsProductsByBranch(
            @PathVariable("branchId") String branchId,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "50") Integer limit,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "clothingSize", required = false) String clothingSize,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "condition", required = false) String condition,
            @RequestParam(value = "availability", required = false) String availability
    ) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.productService.getRekidsProductsByBranchFiltered(
                            branchId,
                            cursor,
                            limit,
                            page,
                            filter,
                            category,
                            clothingSize,
                            brand,
                            condition,
                            availability
                    )));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GenericResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener productos de la sucursal {}", branchId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error al consultar productos de la sucursal en ReKids Sandbox."));
        }
    }

    /**
     * ReKids Sandbox 6: Consultar todos los productos agrupados y separados por sucursal
     * (DEMO La Paz y DEMO Santa Cruz).
     */
    @GetMapping({"/public/rekids/by-branches", "/rekids/by-branches"})
    public ResponseEntity<GenericResponse<List<RekidsBranchCatalogDto>>> getRekidsCatalogSeparatedByBranches(
            @RequestParam(value = "region", required = false) String region
    ) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.productService.getRekidsCatalogSeparatedByBranches(region)));
        } catch (Exception e) {
            log.error("Error al obtener catálogo ReKids separado por sucursales", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error al consultar el catálogo ReKids separado por sucursales."));
        }
    }

    /**
     * ReKids Sandbox 7: Consultar ficha individual de producto (/integrations/v1/products/{productId}).
     */
    @GetMapping({"/public/rekids/products/{productId}", "/rekids/products/{productId}"})
    public ResponseEntity<GenericResponse<RekidsSandboxProductDto>> getRekidsSandboxProductById(
            @PathVariable("productId") String productId
    ) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.productService.getRekidsSandboxProductById(productId)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener ficha del producto {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error al consultar la ficha del producto en ReKids Sandbox."));
        }
    }
}
