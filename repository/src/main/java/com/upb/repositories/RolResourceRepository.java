package com.upb.repositories;


import com.upb.models.rol_resource.RolResource;
import com.upb.models.rol_resource.dto.RolResourceDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolResourceRepository extends JpaRepository<RolResource, String> {
    @Query("SELECT rr from RolResource rr " +
                "INNER JOIN FETCH rr.resource res " +
                "INNER JOIN FETCH rr.rol rol " +
            "WHERE res.name =:name " +
                "AND res.state <> false"
    )
    List<RolResourceDto> findByResourceNameAndResourceStateTrue(@Param("name") String resourceName);

    @Query("SELECT rr from RolResource rr " +
                "INNER JOIN FETCH rr.resource res " +
                "INNER JOIN FETCH rr.rol rol " +
            "WHERE rr.rol.id =:id " +
                "AND res.state <> false"
    )
    List<RolResourceDto> getListByIdRolResourceStateTrue(@Param("id") String idRol);
}
