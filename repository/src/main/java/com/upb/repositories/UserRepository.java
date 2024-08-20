package com.upb.repositories;


import com.upb.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query(" SELECT u FROM User u " +
            " WHERE u.state = 'ACTIVE' AND " +
            " u.email = :email ")
    Optional<User> findByEmailAndStateActive(@Param("email") String email);

}
