package com.upb.toffi.rest;

import com.upb.cores.PermissionService;
import com.upb.models.permission.Permission;
import com.upb.models.permission.dto.PermissionDto;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.permission.SearchPermissionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8084"}, allowCredentials = "true", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})

public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping("")
    public ResponseEntity<GenericResponse<List<PermissionDto>>> getPermissions(@RequestBody SearchPermissionRequest pr) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    this.permissionService.getPermissionsListByAuthenticationIdRol(authentication, pr.getIdRol())
            ));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }

    }
}
