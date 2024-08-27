package com.upb.repositories;


import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.branchOffice.dto.BranchOfficeDto;
import com.upb.models.enterprise.Enterprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchOfficeRepository extends JpaRepository<BranchOffice, String> {
    @Query("SELECT b FROM BranchOffice b " +
            "WHERE b.state <> 'DELETED' " +
                "AND (:name IS NULL OR UPPER(b.name) LIKE :name)"
    )
    Page<BranchOfficeDto> getBranchOfficePageable(@Param("name") String branchOfficeName,
                                                Pageable pageable);
    @Query("SELECT b FROM BranchOffice b " +
                "INNER JOIN FETCH b.enterprise e " +
            "WHERE b.id =:id"
    )
    Optional<BranchOffice> findById(@Param("id") String id);
}
