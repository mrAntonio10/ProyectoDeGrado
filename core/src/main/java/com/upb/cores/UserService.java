package com.upb.cores;

import com.upb.models.branchOffice.dto.BranchOfficeDto;
import com.upb.models.user.User;
import com.upb.models.user.dto.AllUserDataDto;
import com.upb.models.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public interface UserService {
   String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
   Page<UserDto> getUserPageableByBranchOffice(String name, String idBranchOffice, Authentication auth, Pageable pageable);
   User getUserById(String idUser);
   AllUserDataDto getAllUserDataById(String idUser);
   UserDto createUser(String name, String lastname, String password, String phoneNumber,
                      String email, String idRol, String idBranchOffice);
   UserDto updateUser(String id, String name, String lastname, String password, String phoneNumber,
                      String email, String idRol, String state, String idBranchOffice);
   UserDto deleteUserById(String id);

}
