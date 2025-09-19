package com.upb.repositories;


import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.branchOffice.dto.BranchOfficeDto;
import com.upb.models.branchOffice.dto.BranchOfficeStateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchOfficeRepository extends JpaRepository<BranchOffice, String> {
    @Query("SELECT b FROM BranchOffice b " +
                "INNER JOIN FETCH b.enterprise e "+
            "WHERE b.state <> 'DELETED' " +
                "AND e.id =:idE " +
                "AND (:name IS NULL OR UPPER(b.name) LIKE :name)"
    )
    Page<BranchOfficeDto> getBranchOfficeByIdEnterprisePageable(
                                                @Param("idE") String idEnterprise,
                                                @Param("name") String branchOfficeName,
                                                Pageable pageable);

    @Query("SELECT b FROM BranchOffice b " +
                "INNER JOIN FETCH b.enterprise e "+
            "WHERE b.state <> 'DELETED' " +
                "AND (:idE IS NULL OR e.id =:idE) " +
                "AND (:name IS NULL OR UPPER(b.name) LIKE :name)"
    )
    Page<BranchOfficeDto> getBranchOfficePageableForRoot(
                                                @Param("idE") String idEnterprise,
                                                @Param("name") String branchOfficeName,
                                                Pageable pageable);

    @Query("SELECT b FROM BranchOffice b " +
                "INNER JOIN FETCH b.enterprise e " +
            "WHERE b.id =:id"
    )
    Optional<BranchOffice> findById(@Param("id") String id);

    @Query("SELECT b FROM BranchOffice b " +
                "INNER JOIN FETCH b.enterprise e " +
            "WHERE e.id =:idEnterprise " +
                "AND b.state <> 'DELETED'"
    )
    List<BranchOfficeStateDto> getBranchOfficeListByIdEnterprise(@Param("idEnterprise") String idEnterprise);

    @Query("SELECT b.enterprise.id " +
            "FROM BranchOffice b " +
            "JOIN User_BranchOffice ubo ON ubo.branchOffice.id = b.id " +
            "WHERE ubo.user.id = :userId")
    String findEnterpriseIdByUserId(@Param("userId") String userId);
}
