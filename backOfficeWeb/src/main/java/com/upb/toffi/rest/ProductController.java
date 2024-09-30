package com.upb.toffi.rest;

import com.upb.cores.ProductService;
import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductDto;
import com.upb.models.product.dto.ProductListDto;
import com.upb.models.warehouse.dto.WarehousePagedDto;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.product.CreateProductRequest;
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
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8084"}, allowCredentials = "true", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})

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
            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(), new PagedModel<>(
                    (this.productService.getProductsList(filterByProductName, category, pageable))))
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
    public ResponseEntity<GenericResponse<List<ProductListDto>>> getProductListByCategory(@PathVariable("category") String category) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.productService.getProductsListByCategory(category))
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
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    productService.createProduct(product.getName(), product.getCategory(), product.getBeverageFormat())
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
                                    product.getName(), product.getCategory(), product.getBeverageFormat())
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
}
