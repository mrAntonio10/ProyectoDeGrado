package com.upb.cores;

import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductDto;
import com.upb.models.product.dto.ProductListDto;
import com.upb.models.warehouse.dto.WarehousePagedDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    Page<ProductListDto> getProductsList(Authentication auth, String productName, String category, Pageable pageable);
    List<ProductListDto> getProductsListByCategory(Authentication auth, String category);
    Product getProductById(String idProduct);
    ProductDto createProduct(Authentication auth, String name, String category, String beverageFormat, String sku);
    ProductDto updateProduct(String idProduct, String name, String category, String beverageFormat, String sku);

    ProductDto deleteProduct(String idProduct);
}
