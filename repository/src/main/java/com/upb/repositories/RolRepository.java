package com.upb.repositories;


import com.upb.models.rol.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, String> {
    @Query("SELECT r from Rol r " +
            "WHERE r.name =:name " +
                "AND r.state <> false"
    )
    Optional<Rol> findByNameAndStateTrue(@Param("name") String rolName);
}
