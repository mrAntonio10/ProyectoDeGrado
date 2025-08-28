package com.upb.cores.impl;


import com.upb.cores.ResourceService;
import com.upb.models.rol_resource.dto.RolResourceDto;
import com.upb.repositories.RolResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final RolResourceRepository rolResourceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RolResourceDto> getResourceListByAuthenticationIdRol(Authentication authentication) {
        String idRol = authentication.getAuthorities().stream().toList().get(0).toString();

        List<RolResourceDto> resources = rolResourceRepository.getListByIdRolResourceStateTrue(idRol);

        List<RolResourceDto> response = resources.stream()
                .filter(r -> r.getIdParent() == null)
                .sorted((r1, r2) -> r1.getResourcePriority().compareTo(r2.getResourcePriority()))
                .peek(parent -> {
                    List<RolResourceDto> children = resources.stream()
                            .filter(r -> r.getIdParent() != null && r.getIdParent().equals(parent.getIdResource()))
                            .sorted((r1, r2) -> r1.getResourcePriority().compareTo(r2.getResourcePriority()))
                            .peek(child -> child.setUrl(parent.getUrl() + child.getUrl()))
                            .toList();
                    parent.setChildrenResources(children);
                })
                .toList();


        return response;
    }
}
