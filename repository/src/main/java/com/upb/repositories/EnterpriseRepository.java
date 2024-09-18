package com.upb.repositories;


import com.upb.models.enterprise.Enterprise;
import com.upb.models.enterprise.dto.EnterpriseDto;
import com.upb.models.enterprise.dto.EnterpriseStateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
            "WHERE e.id =:id " +
                "AND e.state <> 'DELETED'"
    )
    Optional<Enterprise> findById(@Param("id") String id);

    @Query("SELECT e FROM Enterprise e " +
            "WHERE e.state <> 'DELETED' "
    )
    List<EnterpriseStateDto> getEnterprisesListForRoot();

    @Query("SELECT new com.upb.models.enterprise.dto.EnterpriseStateDto(e) " +
              "FROM Enterprise e " +
            "WHERE e.state <> 'DELETED' " +
                "AND e.id = :idE")
    List<EnterpriseStateDto> getEnterprisesListByUserIdEnterprise(@Param("idE") String idEnterprise);

}
