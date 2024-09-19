package com.upb.repositories;


import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("SELECT p FROM Product p " +
            "WHERE p.id =:id " +
                "AND p.state <> false "
    )
    Optional<Product> findProductByIdAndStateTrue(@Param("id") String id);

    @Query("SELECT p FROM Product p " +
            "WHERE p.state <> false " +
                "AND UPPER(p.category) =:cat"
    )
    List<ProductListDto> getProductsListByCategoryAndStateTrue(@Param("cat") String category);

}
