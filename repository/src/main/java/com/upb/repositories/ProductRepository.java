package com.upb.repositories;

import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductListDto;
import com.upb.models.product.dto.ProductWithImageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    /* Consultas Rekids - Prendas por región, código y sincronización externa */
    @Query("SELECT p FROM Product p WHERE p.externalId = :externalId")
    Optional<Product> findByExternalId(@Param("externalId") String externalId);

    @Query("SELECT p FROM Product p WHERE (UPPER(p.sku) = UPPER(:sku) OR UPPER(COALESCE(p.legacyCode, '')) = UPPER(:sku)) AND p.state <> false " +
            "AND (:region IS NULL OR UPPER(p.region) = :region)")
    Optional<Product> findBySkuAndRegion(@Param("sku") String sku, @Param("region") String region);

    @Query("SELECT new com.upb.models.product.dto.ProductWithImageDto(p) FROM Product p " +
            "WHERE p.state <> false " +
            "AND (:region IS NULL OR UPPER(p.region) = :region) " +
            "AND (:cat IS NULL OR UPPER(p.category) LIKE :cat) " +
            "AND (:filter IS NULL OR UPPER(p.name) LIKE :filter OR UPPER(p.sku) LIKE :filter OR UPPER(COALESCE(p.legacyCode, '')) LIKE :filter OR UPPER(COALESCE(p.brand, '')) LIKE :filter OR UPPER(COALESCE(p.size, '')) LIKE :filter)")
    Page<ProductWithImageDto> getRekidsProductsByRegion(@Param("region") String region,
                                                        @Param("cat") String cat,
                                                        @Param("filter") String filter,
                                                        Pageable pageable);

    @Query("SELECT new com.upb.models.product.dto.ProductWithImageDto(p) FROM Product p " +
            "WHERE p.state <> false " +
            "AND (:region IS NULL OR UPPER(p.region) = :region) " +
            "AND (:cat IS NULL OR UPPER(p.category) LIKE :cat) " +
            "AND (:filter IS NULL OR UPPER(p.name) LIKE :filter OR UPPER(p.sku) LIKE :filter OR UPPER(COALESCE(p.legacyCode, '')) LIKE :filter OR UPPER(COALESCE(p.brand, '')) LIKE :filter OR UPPER(COALESCE(p.size, '')) LIKE :filter) " +
            "AND EXISTS (SELECT 1 FROM com.upb.models.event.EventProduct ep " +
            "            WHERE ep.product.id = p.id AND ep.state = true " +
            "            AND (ep.event.id = :eventId OR UPPER(ep.event.code) = UPPER(:eventId)))")
    Page<ProductWithImageDto> getRekidsProductsByRegionAndEvent(@Param("region") String region,
                                                                @Param("eventId") String eventId,
                                                                @Param("cat") String cat,
                                                                @Param("filter") String filter,
                                                                Pageable pageable);

    @Query("SELECT new com.upb.models.product.dto.ProductWithImageDto(p) FROM Product p " +
            "WHERE p.state <> false " +
            "AND (:region IS NULL OR UPPER(p.region) = :region) " +
            "AND (:branchId IS NULL OR UPPER(COALESCE(p.branchId, '')) = :branchId) " +
            "AND (:cat IS NULL OR UPPER(p.category) LIKE :cat) " +
            "AND (:size IS NULL OR UPPER(COALESCE(p.size, '')) LIKE :size) " +
            "AND (:brand IS NULL OR UPPER(COALESCE(p.brand, '')) LIKE :brand) " +
            "AND (:condition IS NULL OR UPPER(COALESCE(p.condition, '')) LIKE :condition OR UPPER(COALESCE(p.color, '')) LIKE :condition) " +
            "AND (:availability IS NULL OR UPPER(COALESCE(p.availability, 'AVAILABLE')) = :availability) " +
            "AND (:filter IS NULL OR UPPER(p.name) LIKE :filter OR UPPER(p.sku) LIKE :filter " +
            "     OR UPPER(COALESCE(p.legacyCode, '')) LIKE :filter OR UPPER(COALESCE(p.brand, '')) LIKE :filter " +
            "     OR UPPER(COALESCE(p.size, '')) LIKE :filter OR UPPER(COALESCE(p.category, '')) LIKE :filter " +
            "     OR UPPER(COALESCE(p.condition, '')) LIKE :filter)")
    Page<ProductWithImageDto> getRekidsProductsFilteredPageable(
            @Param("region") String region,
            @Param("branchId") String branchId,
            @Param("cat") String cat,
            @Param("size") String size,
            @Param("brand") String brand,
            @Param("condition") String condition,
            @Param("availability") String availability,
            @Param("filter") String filter,
            Pageable pageable);

    @Query("SELECT new com.upb.models.product.dto.ProductWithImageDto(p) FROM Product p " +
            "WHERE p.state <> false " +
            "AND (:region IS NULL OR UPPER(p.region) = :region) " +
            "AND (:branchId IS NULL OR UPPER(COALESCE(p.branchId, '')) = :branchId) " +
            "AND (:cat IS NULL OR UPPER(p.category) LIKE :cat) " +
            "AND (:size IS NULL OR UPPER(COALESCE(p.size, '')) LIKE :size) " +
            "AND (:brand IS NULL OR UPPER(COALESCE(p.brand, '')) LIKE :brand) " +
            "AND (:condition IS NULL OR UPPER(COALESCE(p.condition, '')) LIKE :condition OR UPPER(COALESCE(p.color, '')) LIKE :condition) " +
            "AND (:availability IS NULL OR UPPER(COALESCE(p.availability, 'AVAILABLE')) = :availability) " +
            "AND (:filter IS NULL OR UPPER(p.name) LIKE :filter OR UPPER(p.sku) LIKE :filter " +
            "     OR UPPER(COALESCE(p.legacyCode, '')) LIKE :filter OR UPPER(COALESCE(p.brand, '')) LIKE :filter " +
            "     OR UPPER(COALESCE(p.size, '')) LIKE :filter OR UPPER(COALESCE(p.category, '')) LIKE :filter " +
            "     OR UPPER(COALESCE(p.condition, '')) LIKE :filter) " +
            "AND ( " +
            "     (:eventIdUpper = 'NOVEDADES' AND p.isNew = true) " +
            "     OR (:eventIdUpper = 'DESCUENTOS' AND p.discountValue IS NOT NULL AND p.discountValue > 0) " +
            "     OR EXISTS (SELECT 1 FROM com.upb.models.event.EventProduct ep " +
            "                WHERE ep.product.id = p.id AND ep.state = true " +
            "                AND (ep.event.id = :eventId OR UPPER(ep.event.code) = :eventIdUpper OR UPPER(ep.event.name) = :eventIdUpper)) " +
            ")")
    Page<ProductWithImageDto> getRekidsProductsFilteredByEventPageable(
            @Param("region") String region,
            @Param("branchId") String branchId,
            @Param("eventId") String eventId,
            @Param("eventIdUpper") String eventIdUpper,
            @Param("cat") String cat,
            @Param("size") String size,
            @Param("brand") String brand,
            @Param("condition") String condition,
            @Param("availability") String availability,
            @Param("filter") String filter,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.state <> false " +
            "AND (:region IS NULL OR UPPER(p.region) = UPPER(:region)) " +
            "ORDER BY p.receptionDate DESC")
    List<Product> findAllActiveByRegion(@Param("region") String region);

    @Modifying
    @Query("UPDATE Product p SET p.state = false WHERE p.state <> false AND (UPPER(p.sku) LIKE 'RK-SCZ-%' OR UPPER(p.sku) LIKE 'RK-LPZ-%' OR UPPER(COALESCE(p.externalId, '')) LIKE 'EXT-%')")
    int deactivateMockedGarments();

}
