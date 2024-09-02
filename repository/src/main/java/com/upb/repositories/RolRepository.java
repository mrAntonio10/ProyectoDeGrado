package com.upb.repositories;


import com.upb.models.rol.Rol;
import com.upb.models.rol.dto.RolForListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, String> {
    @Query("SELECT r FROM Rol r " +
            "WHERE r.name =:name " +
                "AND r.state <> false"
    )
    Optional<Rol> findByNameAndStateTrue(@Param("name") String rolName);

    @Query("SELECT r FROM Rol r " +
            "WHERE r.id =:id " +
            "AND r.state <> false"
    )
    Optional<Rol> findByIdAndStateTrue(@Param("id") String idRol);

    @Query("SELECT r from Rol r" +
            " WHERE r.state <> false ")
    List<RolForListDto> getRolListForRoot();
}
