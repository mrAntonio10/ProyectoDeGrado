package com.upb.toffi.rest;

import com.upb.cores.UserService;
import com.upb.models.branchOffice.dto.BranchOfficeDto;
import com.upb.models.branchOffice.dto.BranchOfficeStateDto;
import com.upb.models.user.User;
import com.upb.models.user.dto.AllUserDataDto;
import com.upb.models.user.dto.UserDto;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.user.CreateUserRequest;
import com.upb.toffi.rest.request.user.UpdateUserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8084"}, allowCredentials = "true", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})

public class UserController {
    private final UserService userService;

    @GetMapping("/logout")
    public ResponseEntity<GenericResponse<?>> getLogoutPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.userService.logout(request, response, authentication)));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @GetMapping("")
    public ResponseEntity<GenericResponse<PagedModel<UserDto>>> getUserPageable(@RequestParam(value = "filter", defaultValue = "") String filterByName,
                                                                                @RequestParam(value = "idBranchOffice", defaultValue = "") String idBranchOffice,
                                                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                @RequestParam(value = "size", defaultValue = "5") Integer pageSize,
                                                                                @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                                                @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
    ) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(), new PagedModel<>(
                    (this.userService.getUserPageableByBranchOffice(filterByName, idBranchOffice, authentication, pageable))))
            );
        } catch (NullPointerException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<GenericResponse<AllUserDataDto>> getUserById(@PathVariable("id") String idUser
    ) {
        try {

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.userService.getAllUserDataById(idUser))
            );
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idUser, e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @PostMapping()
    public ResponseEntity<GenericResponse<UserDto>> createUser(@RequestBody CreateUserRequest ur) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    userService.createUser(ur.getName(), ur.getLastname(), ur.getPassword(),ur.getPhoneNumber(),
                            ur.getEmail(), ur.getIdRol(), ur.getIdBranchOffice()))
            );
        }catch (NoSuchElementException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @PutMapping()
    public ResponseEntity<GenericResponse<UserDto>> updateUser(@RequestBody UpdateUserRequest ur) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    userService.updateUser(ur.getId(), ur.getName(), ur.getLastname(), ur.getPassword(),ur.getPhoneNumber(),
                            ur.getEmail(), ur.getIdRol(), ur.getState(), ur.getIdBranchOffice()))
            );
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), ur.getId(),e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @DeleteMapping("/{id-user}")
    public ResponseEntity<GenericResponse<UserDto>> deleteUser(@PathVariable("id-user") String idUser) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    userService.deleteUserById(idUser)
            ));
        } catch (NoSuchElementException e) {
            log.error("Error {} ID: {}, causa {}", e.getMessage(), idUser,e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }
}
