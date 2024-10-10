package com.upb.repositories;


import com.upb.models.warehouse.Warehouse;
import com.upb.models.warehouse.dto.WarehousePageableProductsDto;
import com.upb.models.warehouse.dto.WarehousePagedDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
    @Query("SELECT w FROM Warehouse w " +
                "INNER JOIN FETCH w.product p " +
                "INNER JOIN FETCH w.branchOffice b " +
                "INNER JOIN FETCH b.enterprise e " +
            "WHERE w.state <> 'DELETED' " +
                "AND b.state <> 'DELETED' " +
                "AND p.state = true " +
                "AND (:pName IS NULL OR UPPER(p.name) LIKE :pName) " +
                "AND (:category IS NULL OR UPPER(p.category) LIKE :category) " +
                "AND (:idBranchOffice IS NULL OR b.id = :idBranchOffice) " +
                "AND e.id = :idEnterprise " +
                "AND (:limit IS NULL " +
                "     OR (:limit = 'MAX' AND w.maxProduct > w.stock) " +
                "     OR (:limit = 'MIN' AND w.minProduct > w.stock))"
    )
    Page<WarehousePagedDto> getWarehousePageable(@Param("idEnterprise") String idEnterprise,
                                                 @Param("idBranchOffice") String idBranchOffice,
                                                 @Param("pName") String productName,
                                                 @Param("category") String category,
                                                 @Param("limit") String limit,
                                                 Pageable pageable);

    @Query("SELECT w FROM Warehouse w " +
                "INNER JOIN FETCH w.product p " +
                "INNER JOIN FETCH w.branchOffice b " +
            "WHERE w.state <> 'DELETED' " +
                "AND b.state <> 'DELETED' " +
                "AND p.state = true " +
                "AND (:pName IS NULL OR UPPER(w.productCode) LIKE :pName OR UPPER(p.name) LIKE :pName) " +
                "AND (:category IS NULL OR UPPER(p.category) LIKE :category) " +
                "AND (b.id = :idBranchOffice)"
    )
    Page<WarehousePageableProductsDto> getWarehousePageableProducts(@Param("idBranchOffice") String idBranchOffice,
                                                                    @Param("pName") String productName,
                                                                    @Param("category") String category,
                                                                    Pageable pageable);

    @Query("SELECT w FROM Warehouse w " +
                "INNER JOIN FETCH w.product p " +
                "INNER JOIN FETCH w.branchOffice b " +
            "WHERE w.state <> 'DELETED' " +
                "AND b.state <> 'DELETED' " +
                "AND p.state = true " +
                "AND w.id =:id"
    )
    Optional<Warehouse> findWarehouseByIdAndStateTrue(@Param("id") String id);

    @Query("SELECT w FROM Warehouse w " +
                "INNER JOIN FETCH w.product p " +
                "INNER JOIN FETCH w.branchOffice b " +
            "WHERE w.state <> 'DELETED' " +
                "AND b.state <> 'DELETED' " +
                "AND p.state = true " +
                "AND UPPER(p.name) =:productName " +
                "AND (:bFormat IS NULL OR UPPER(p.beverageFormat) =:bFormat) " +
                "AND b.id =:idBranchOffice"
    )
    Optional<Warehouse> findWarehouseByIdBranchOfficeProductNameBeverageFormatAndStateTrue(@Param("idBranchOffice") String idBranchOffice,
                                                                                           @Param("productName") String productName,
                                                                                           @Param("bFormat") String beverageFormat);

}
