package com.upb.repositories;


import com.upb.models.permission.Permission;
import com.upb.models.permission.dto.PermissionDto;
import com.upb.models.permission.dto.ResourcePermissionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    @Query("SELECT p FROM Permission p " +
                "INNER JOIN FETCH p.rol r " +
                "INNER JOIN FETCH p.resource res " +
                "INNER JOIN FETCH p.operation op " +
            "WHERE (:idRol IS NULL OR r.id =:idRol)"
    )
    List<PermissionDto> getPermissionByIdRolList(@Param("idRol") String idRol);

    @Query("SELECT p FROM Permission p " +
                "INNER JOIN FETCH p.rol r " +
                "INNER JOIN FETCH p.resource res " +
                "INNER JOIN FETCH p.operation op " +
            "WHERE r.id =:idRol")
    List<PermissionDto> getPermissionList(@Param("idRol") String idRol);


    @Query("SELECT p FROM Permission p " +
                "INNER JOIN FETCH p.rol r " +
                "INNER JOIN FETCH p.resource res " +
                "INNER JOIN FETCH p.operation op " +
            "WHERE r.id =:idRol " +
                "AND res.url =:url " +
                "AND p.state <> false " +
                "AND op.state <> false "
    )
    List<ResourcePermissionDto> getPermissionByIdRolAndResourceUrl(@Param("idRol") String idRol,
                                                                   @Param("url") String resourceUrl);

    @Query("SELECT p FROM Permission p " +
                "INNER JOIN FETCH p.resource res " +
                "INNER JOIN FETCH p.operation op " +
            "WHERE p.id =:id"
    )
    Optional<Permission> finPermissionById(@Param("id") String id);

}
