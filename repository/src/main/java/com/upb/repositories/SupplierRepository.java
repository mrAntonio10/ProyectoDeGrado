package com.upb.repositories;

import com.upb.models.supplier.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, String> {

    @Query("SELECT s FROM Supplier s WHERE s.state <> 'DELETED' AND s.enterprise.id = :enterpriseId")
    List<Supplier> findAllActiveByEnterpriseId(@Param("enterpriseId") String enterpriseId);
}
