package com.upb.repositories;


import com.upb.models.user.User;
import com.upb.models.user.dto.AllUserDataDto;
import com.upb.models.user.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u " +
            "WHERE u.state <> 'DELETED' " +
                "AND (:name IS NULL OR u.name LIKE :name)"
    )
    Page<UserDto> getUserPageable(@Param("name") String name,
                                  Pageable pageable);

    @Query(" SELECT u FROM User u " +
            " WHERE u.state = 'ACTIVE' AND " +
            "u.email = :email"
    )
    Optional<User> findByEmailAndStateActive(@Param("email") String email);

    @Query(" SELECT u FROM User u " +
            " WHERE u.id = :id "
    )
    Optional<User> findUserById(@Param("id") String id);

}
