package com.upb.cores.impl;


import com.upb.cores.ProductService;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductDto;
import com.upb.models.product.dto.ProductListDto;
import com.upb.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductListDto> getProductsListByCategory(String category) {
        return productRepository.getProductsListByCategoryAndStateTrue(category.toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(String idProduct) {
        return productRepository.findProductByIdAndStateTrue(idProduct).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes al producto")
        );
    }

    @Override
    public ProductDto createProduct(String name, String category, String beverageFormat) {
        StringUtilMod.notNullStringMaxLength(name, 60, "nombre");
        StringUtilMod.notNullStringMaxLength(category, 30, "categoría");

        if(category.equalsIgnoreCase("BEBIDA")) {
            StringUtilMod.notNullStringMaxLength(beverageFormat, 30, "formato bebida");
        }

        Product product = Product.builder()
                .name(name)
                .category(StringUtilMod.capitalizeFirstLetter(category))
                .beverageFormat(StringUtilMod.capitalizeFirstLetter(beverageFormat))
                .state(true)
                .build();

        productRepository.save(product);

        return new ProductDto(product);
    }

    @Override
    public ProductDto updateProduct(String idProduct, String name, String category, String beverageFormat) {
        StringUtilMod.notNullStringMaxLength(name, 60, "nombre");
        StringUtilMod.notNullStringMaxLength(category, 30, "categoría");

        if(category.equalsIgnoreCase("BEBIDA")) {
            StringUtilMod.notNullStringMaxLength(beverageFormat, 30, "formato bebida");
        } else {
            beverageFormat = "";
        }

        Product product = productRepository.findProductByIdAndStateTrue(idProduct).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes al producto")
        );

        product.setName(name);
        product.setCategory(category);
        product.setBeverageFormat(beverageFormat);

        productRepository.save(product);

        return new ProductDto(product);
    }

    @Override
    public ProductDto deleteProduct(String idProduct) {
        Product product = productRepository.findProductByIdAndStateTrue(idProduct).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes al producto")
        );

        product.setState(false);
        productRepository.save(product);

        return new ProductDto(product);
    }
}
