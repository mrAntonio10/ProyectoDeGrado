package com.upb.cores.impl;


import com.upb.cores.PermissionService;
import com.upb.cores.RolService;
import com.upb.models.operation.dto.OperationDto;
import com.upb.models.permission.dto.PermissionDto;
import com.upb.models.rol.Rol;
import com.upb.models.rol.dto.RolForListDto;
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
public class RolServiceImpl implements RolService {
    private final RolRepository rolRepository;
    @Override
    @Transactional(readOnly = true)
    public List<RolForListDto> getRolByAuth(Authentication authentication) {
        String idRol = authentication.getAuthorities().stream().toList().get(0).toString();

        Rol rol = rolRepository.findByIdAndStateTrue(idRol).orElseThrow(
                () -> new NoSuchElementException("No se encontró el rol solicitado")
        );

        return this.rolRepository.getRolListForRoot();
    }

    @Override
    @Transactional(readOnly = true)
    public Rol getRolById(String idRol) {
        return rolRepository.findByIdAndStateTrue(idRol).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores para el rol correspondiente"));
    }
}
