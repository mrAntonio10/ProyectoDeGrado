package com.upb.repositories;


import com.upb.models.user.User;
import com.upb.models.user.dto.UserDto;
import com.upb.models.user_branchOffice.User_BranchOffice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBranchOfficeRepository extends JpaRepository<User_BranchOffice, String> {
    @Query("SELECT u_b FROM User_BranchOffice u_b " +
                "INNER JOIN FETCH u_b.user u " +
                "INNER JOIN FETCH u_b.branchOffice b " +
            "WHERE u.state <> 'DELETED' " +
                "AND b.state <> 'DELETED' " +
                "AND (:name IS NULL OR u.name LIKE :name) " +
                "AND b.id =:idBranchOffice"
    )
    Page<UserDto> getUserPageableByIdBranchOffice(@Param("name") String name,
                                  @Param("idBranchOffice") String idBranchOffice,
                                  Pageable pageable);

}
