package com.upb.repositories;


import com.upb.models.enterprise.Enterprise;
import com.upb.models.enterprise.dto.EnterpriseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, String> {
    @Query("SELECT e FROM Enterprise e " +
            "WHERE e.state <> 'DELETED' " +
                "AND (:name IS NULL OR UPPER(e.name) LIKE :name)"
    )
    Page<EnterpriseDto> getEnterprisePageable(@Param("name") String enterpriseName,
                                              Pageable pageable);
    @Query("SELECT e FROM Enterprise e " +
            "WHERE e.id =:id"
    )
    Optional<Enterprise> findById(@Param("id") String id);
}
