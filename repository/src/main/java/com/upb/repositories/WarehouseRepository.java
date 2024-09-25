package com.upb.repositories;


import com.upb.models.enterprise.dto.EnterpriseDto;
import com.upb.models.user.User;
import com.upb.models.warehouse.Warehouse;
import com.upb.models.warehouse.dto.WarehouseDto;
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
            "WHERE w.state <> 'DELETED' " +
                "AND b.state <> 'DELETED' " +
                "AND p.state = true " +
                "AND (:pName IS NULL OR UPPER(p.name) LIKE :pName) " +
                "AND (:category IS NULL OR UPPER(p.category) LIKE :category)"
    )
    Page<WarehousePagedDto> getEnterprisePageable(@Param("pName") String productName,
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
    Optional<Warehouse> findWarehouseByIdAndStateNotDeleted(@Param("id") String id);

}
