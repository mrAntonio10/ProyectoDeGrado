package com.upb.repositories;


import com.upb.models.operation.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, String> {

    @Query("SELECT o FROM Operation o " +
            "WHERE o.name =:name"
    )
    Optional<Operation> findOperationByName(@Param("name") String operationName);
}
