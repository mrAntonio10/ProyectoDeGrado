package com.upb.repositories;


import com.upb.models.branchOffice.dto.BranchOfficeDto;
import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("SELECT p FROM Product p " +
            "WHERE p.state <> false " +
                "AND (:cat IS NULL OR UPPER(p.category) LIKE :cat) " +
                "AND (:name IS NULL OR UPPER(p.name) LIKE :name )"
    )
    Page<ProductListDto> getProductPageable(
                                        @Param("name") String productName,
                                        @Param("cat") String cat,
                                        Pageable pageable);

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

    @Query("SELECT p FROM Product p " +
            "WHERE p.id IN :list " +
                "AND p.state <> false")
    List<Product> getProductListByIdList(@Param("list") List<String> idList);

}
