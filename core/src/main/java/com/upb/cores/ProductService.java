package com.upb.cores;

import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductDto;
import com.upb.models.product.dto.ProductListDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    List<ProductListDto> getProductsListByCategory(String category);
    Product getProductById(String idProduct);
    ProductDto createProduct(String name, String category, String beverageFormat);
    ProductDto updateProduct(String idProduct, String name, String category, String beverageFormat);

    ProductDto deleteProduct(String idProduct);
}
