package com.upb.repositories;

import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductListDto;
import com.upb.models.product.dto.ProductWithImageDto;
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
            "INNER JOIN FETCH p.enterprise e " +
            "WHERE p.state <> false " +
            "AND (:cat IS NULL OR UPPER(p.category) LIKE :cat) " +
            "AND (:name IS NULL OR UPPER(p.name) LIKE :name )" +
            "AND e.id =:idEnterprise")
    Page<ProductListDto> getProductPageable(@Param("idEnterprise") String idEnterprise,
            @Param("name") String productName,
            @Param("cat") String cat,
            Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.id =:id " +
            "AND p.state <> false ")
    Optional<Product> findProductByIdAndStateTrue(@Param("id") String id);

    @Query("SELECT p FROM Product p " +
            "INNER JOIN FETCH p.enterprise e " +
            "WHERE p.state <> false " +
            "AND e.id =:idEnterprise " +
            "AND UPPER(p.category) =:cat")
    List<ProductListDto> getEnterpriseProductsListByCategoryAndStateTrue(@Param("idEnterprise") String idEnterprise,
            @Param("cat") String category);

    @Query("SELECT p FROM Product p " +
            "WHERE p.id IN :list " +
            "AND p.state <> false")
    List<Product> getProductsListByIdList(@Param("list") List<String> idList);

    @Query("SELECT new com.upb.models.product.dto.ProductWithImageDto(p, w.stock, w.unitaryCost) FROM Product p " +
            "INNER JOIN p.enterprise e " +
            "INNER JOIN com.upb.models.warehouse.Warehouse w ON w.product.id = p.id AND w.state <> 'INACTIVO' " +
            "WHERE p.state <> false " +
            "AND w.branchOffice.id = :idBranchOffice " +
            "AND w.stock > 0 " +
            "AND (:cat IS NULL OR UPPER(p.category) LIKE :cat) " +
            "AND (:name IS NULL OR UPPER(p.name) LIKE :name )" +
            "AND e.id =:idEnterprise")
    Page<ProductWithImageDto> getProductWithImagePageable(@Param("idEnterprise") String idEnterprise,
            @Param("idBranchOffice") String idBranchOffice,
            @Param("name") String productName,
            @Param("cat") String cat,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p " +
            "INNER JOIN p.enterprise e " +
            "WHERE p.state <> false " +
            "AND UPPER(p.category) = UPPER(:category) " +
            "AND e.id = :enterpriseId")
    Long countProductsByCategoryAndEnterprise(@Param("category") String category,
            @Param("enterpriseId") String enterpriseId);

}
