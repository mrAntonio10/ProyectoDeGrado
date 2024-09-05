package com.upb.cores.impl;


import com.upb.cores.PermissionService;
import com.upb.models.operation.dto.OperationDto;
import com.upb.models.permission.Permission;
import com.upb.models.permission.dto.PermissionDto;
import com.upb.models.permission.dto.PermissionResponseDto;
import com.upb.models.permission.dto.ResourcePermissionDto;
import com.upb.models.rol.Rol;
import com.upb.repositories.PermissionRepository;
import com.upb.repositories.RolRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionDto> getPermissionsListByAuthenticationIdRol(Authentication authentication, String idRolRequest) {
        String idRol = authentication.getAuthorities().stream().toList().get(0).toString();

        Rol rol = rolRepository.findByIdAndStateTrue(idRol).orElseThrow(
                () -> new NoSuchElementException("No se encontró el rol solicitado")
        );

        List<PermissionDto> permissionsList;
        if(rol.getName().equals("ROOT")) {
            permissionsList = permissionRepository.getPermissionByIdRolList(idRolRequest);
        } else {
            permissionsList = permissionRepository.getPermissionList(rol.getId());
        }

        return permissionsList.stream()
                .collect(Collectors.groupingBy(
                        PermissionDto::getIdResource,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    PermissionDto first = list.get(0); // Usa el primero como base
                                    List<OperationDto> operations = list.stream()
                                            .map(PermissionDto::getOperationDto)
                                            .collect(Collectors.toList());
                                    return PermissionDto.builder()
                                            .idResource(first.getIdResource())
                                            .resourceName(first.getResourceName())
                                            .operationDtoList(operations) // Todas las operaciones de ese recurso
                                            .build();
                                }
                        )
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourcePermissionDto> getPermissionsByAuthenticationAndResourceUrl(Authentication authentication, String resourceUrl) {
        String idRol = authentication.getAuthorities().stream().toList().get(0).toString();

        Rol rol = rolRepository.findByIdAndStateTrue(idRol).orElseThrow(
                () -> new NoSuchElementException("No se encontró el rol solicitado")
        );

        return permissionRepository.getPermissionByIdRolAndResourceUrl(rol.getId(), "/"+resourceUrl);
    }

    @Override
    public String modifyPermissionState(String idPermisison, Boolean state) {
        return null;
    }
}
