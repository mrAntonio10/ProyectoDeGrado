package com.upb.repositories;

import com.upb.models.utils.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<Domain, String> {

    @Query("SELECT d FROM Domain d " +
            "WHERE d.enterprise.id = :enterpriseId " +
            "AND d.domain = :domain " +
            "AND d.isDeleted = false ")
    List<Domain> findByEnterpriseIdAndDomainAndIsDeletedFalse(@Param("enterpriseId") String enterpriseId,
            @Param("domain") String domain);

    @Query("SELECT d FROM Domain d " +
            "WHERE d.enterprise.id = :enterpriseId " +
            "AND d.domain = :domain " +
            "AND UPPER(d.name) = :name " +
            "AND d.isDeleted = false")
    Optional<Domain> findByEnterpriseIdAndDomainAndNameIgnoreCaseAndIsDeletedFalse(
            @Param("enterpriseId") String enterpriseId, @Param("domain") String domain, @Param("name") String name);
}
