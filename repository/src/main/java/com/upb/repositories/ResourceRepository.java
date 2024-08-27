package com.upb.repositories;


import com.upb.models.resource.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String> {
    @Query("SELECT res FROM Resource res " +
            "WHERE res.name =:name " +
                "AND res.state <> false"
    )
    Optional<Resource> findByNameAndStateTrue(@Param("name") String resourceName);
}
