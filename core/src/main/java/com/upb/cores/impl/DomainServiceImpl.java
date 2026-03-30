package com.upb.cores.impl;

import com.upb.cores.DomainService;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.models.utils.Domain;
import com.upb.repositories.DomainRepository;
import com.upb.repositories.ProductRepository;
import com.upb.repositories.UserBranchOfficeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;
    private final ProductRepository productRepository;
    private final UserBranchOfficeRepository userBranchOfficeRepository;

    private Enterprise getEnterpriseFromAuth(Authentication auth) {
        String idRol = auth.getAuthorities().stream().toList().get(0).toString();
        User user = (User) auth.getPrincipal();
        List<User_BranchOffice> ub = userBranchOfficeRepository.getUser_BranchOfficeByIdUserAndIdRol(user.getId(),
                idRol);
        if (ub == null || ub.isEmpty()) {
            throw new NoSuchElementException("Usuario no asociado a ninguna sucursal/empresa");
        }
        return ub.get(0).getBranchOffice().getEnterprise();
    }

    @Override
    @Transactional(readOnly = true)
    public Long checkDomainValueUsage(String domainId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new NoSuchElementException("Dominio no encontrado."));
        // Currently only products are linked to categories. Might need to expand this if other domains have relational dependencies.
        return productRepository.countProductsByCategoryAndEnterprise(domain.getName(), domain.getEnterprise().getId());
    }

    // --- Admin Domain Endpoints ---

    @Override
    @Transactional(readOnly = true)
    public List<String> getMasterDomains(Authentication auth) {
        Enterprise enterprise = getEnterpriseFromAuth(auth);
        List<Domain> systemDomains = domainRepository.findDomainsFromEnterprise(enterprise.getId());
        return systemDomains.stream().map(Domain::getName).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.upb.models.utils.dto.DomainAdminDto> getDomainValues(Authentication auth, String domainName) {
        Enterprise enterprise = getEnterpriseFromAuth(auth);
        List<Domain> domains = domainRepository.findByEnterpriseIdAndDomainAndIsDeletedFalse(enterprise.getId(), domainName);
        return domains.stream().map(com.upb.models.utils.dto.DomainAdminDto::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public com.upb.models.utils.dto.DomainAdminDto createDomainValue(Authentication auth, String domainName, String valueName, String description) {
        // Find existing value
        Enterprise enterprise = getEnterpriseFromAuth(auth);
        Optional<Domain> existOpt = domainRepository.findByEnterpriseIdAndDomainAndNameIgnoreCaseAndIsDeletedFalse(
                enterprise.getId(), domainName, valueName);
        if (existOpt.isPresent()) {
            throw new IllegalArgumentException("Ya existe este valor para el dominio.");
        }

        Domain domain = Domain.builder()
                .domain(domainName)
                .name(valueName)
                .description(description)
                .enterprise(enterprise)
                .isDeleted(false)
                .build();

        domain = domainRepository.save(domain);
        return new com.upb.models.utils.dto.DomainAdminDto(domain);
    }

    @Override
    @Transactional
    public com.upb.models.utils.dto.DomainAdminDto updateDomainValue(Authentication auth, String id, String valueName, String description) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Valor de Dominio no encontrado."));

        // Check name clash
        Optional<Domain> existOpt = domainRepository.findByEnterpriseIdAndDomainAndNameIgnoreCaseAndIsDeletedFalse(
                domain.getEnterprise().getId(), domain.getDomain(), valueName);
        
        if (existOpt.isPresent() && !existOpt.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe este nombre en este dominio.");
        }

        domain.setName(valueName);
        domain.setDescription(description);
        domain = domainRepository.save(domain);
        return new com.upb.models.utils.dto.DomainAdminDto(domain);
    }

    @Override
    @Transactional
    public void deleteDomainValue(Authentication auth, String id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Valor de Dominio no encontrado."));
        
        domain.setIsDeleted(true);
        domainRepository.save(domain);
    }
}
