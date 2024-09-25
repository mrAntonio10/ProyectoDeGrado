package com.upb.repositories;


import com.upb.models.user.User;
import com.upb.models.user.dto.AllUserDataDto;
import com.upb.models.user.dto.UserDto;
import com.upb.models.user_branchOffice.User_BranchOffice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBranchOfficeRepository extends JpaRepository<User_BranchOffice, String> {
    @Query("SELECT u_b FROM User_BranchOffice u_b " +
                "INNER JOIN FETCH u_b.user u " +
                "INNER JOIN FETCH u_b.branchOffice b " +
            "WHERE u.state <> 'DELETED' " +
                "AND b.state <> 'DELETED' " +
                "AND (:name IS NULL OR UPPER(u.name) LIKE :name OR UPPER(u.lastname) LIKE :name) " +
                "AND b.id =:idBranchOffice"
    )
    Page<UserDto> getUserPageableByIdBranchOffice(@Param("name") String name,
                                  @Param("idBranchOffice") String idBranchOffice,
                                  Pageable pageable);

    @Query("SELECT u_b FROM User_BranchOffice u_b " +
                "INNER JOIN FETCH u_b.user u " +
                "INNER JOIN FETCH u_b.branchOffice b " +
            "WHERE u.state <> 'DELETED' " +
                "AND b.state <> 'DELETED' " +
                "AND (:name IS NULL OR UPPER(u.name) LIKE :name OR UPPER(u.lastname) LIKE :name) " +
                "AND (:idBranchOffice IS NULL OR b.id =:idBranchOffice)"
    )
    Page<UserDto> getUserPageableByIdBranchOfficeForRoot(@Param("name") String name,
                                                  @Param("idBranchOffice") String idBranchOffice,
                                                  Pageable pageable);

    @Query("SELECT ub FROM User_BranchOffice ub " +
                "INNER JOIN FETCH ub.user u " +
                "INNER JOIN FETCH ub.branchOffice b "+
            "WHERE u.state <> 'DELETED' " +
                "AND u.rol.id = :idRol"
            )
    List<User_BranchOffice> findUser_BranchOfficeByIdUserRol(@Param("idRol") String idRol);

    @Query("SELECT ub FROM User_BranchOffice ub " +
                "INNER JOIN FETCH ub.user u " +
                "INNER JOIN FETCH ub.branchOffice b "+
            "WHERE u.state <> 'DELETED' " +
             "AND u.id = :idUser "
    )
    Optional<User_BranchOffice> findUser_BranchOfficeByIdUser(@Param("idUser") String idUser);


    @Query("SELECT ub FROM User_BranchOffice ub " +
                "INNER JOIN FETCH ub.user u " +
                "INNER JOIN FETCH ub.branchOffice b "+
                "INNER JOIN FETCH b.enterprise e " +
            "WHERE u.state <> 'DELETED' " +
                "AND u.id = :idUser"
    )
    Optional<AllUserDataDto> getUser_BranchOfficeDataByIdUser(@Param("idUser") String idUser);

}
