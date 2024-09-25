package com.upb.cores.impl;


import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.EnterpriseService;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.enterprise.dto.EnterpriseDto;
import com.upb.models.enterprise.dto.EnterpriseStateDto;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.repositories.EnterpriseRepository;
import com.upb.repositories.UserBranchOfficeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnterpriseServiceImpl implements EnterpriseService {
    private final EnterpriseRepository enterpriseRepository;
    private final UserBranchOfficeRepository userBranchOfficeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<EnterpriseDto> getEnterprisePageable(String name, Pageable pageable) {
       name = (!StringUtil.isNullOrEmpty(name) ? "%" +name.toUpperCase()+ "%" : null);

        return enterpriseRepository.getEnterprisePageable(name, pageable);
    }

    @Override
    public EnterpriseDto createEnterprise(String name, String email, String phoneNumber, String logo, String description) {
        StringUtilMod.notNullStringMaxLength(name, 120,"nombre");
        StringUtilMod.notNullEmailMatcher(email, "email");
        StringUtilMod.throwStringIsNullOrEmpty(description, "descripción");
        StringUtilMod.notNullNumberMatcherMaxLength(phoneNumber, 20,"número telefónico");

        Enterprise enterprise = Enterprise.builder()
                .name(name)
                .email(email)
                .description(description)
                .phoneNumber(phoneNumber)
                .state("ACTIVE")
                .logo(logo)
                .build();

        enterpriseRepository.save(enterprise);

        return new EnterpriseDto(enterprise);
    }

    @Override
    public EnterpriseDto updateEnterprise(String id, String name, String email, String phoneNumber, String logo, String description, String state) {
        StringUtilMod.notNullStringMaxLength(name, 120,"nombre");
        StringUtilMod.notNullEmailMatcher(email, "email");
        StringUtilMod.throwStringIsNullOrEmpty(description, "descripción");
        StringUtilMod.notNullNumberMatcherMaxLength(phoneNumber, 20,"número telefónico");

        Enterprise ent = enterpriseRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No fue posible recuperar los valores correspondientes a la empresa."));

        ent.setName(name);
        ent.setDescription(description);
        ent.setEmail(email);
        ent.setPhoneNumber(phoneNumber);
        ent.setLogo(logo);
        ent.setState(state);

        enterpriseRepository.save(ent);

        return new EnterpriseDto(ent);
    }

    @Override
    public EnterpriseStateDto deleteEnterpriseById(String id) {
        Enterprise ent = enterpriseRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No fue posible recuperar los valores correspondientes a la empresa."));

        ent.setState("DELETED");

        enterpriseRepository.save(ent);

        return new EnterpriseStateDto(ent);
    }

    @Override
    @Transactional(readOnly = true)
    public Enterprise getEnterpriseById(String idEnterprise) {
        return this.enterpriseRepository.findById(idEnterprise).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes a la empresa."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnterpriseStateDto> getEnterpriseCombo(Authentication auth) {
        String idRol = auth.getAuthorities().stream().toList().get(0).toString();

        List<User_BranchOffice> ub = userBranchOfficeRepository.findUser_BranchOfficeByIdUserRol(idRol);

        if(!ub.isEmpty()) {
            log.info("Empresa de un usuario");
            return enterpriseRepository.getEnterprisesListByUserIdEnterprise(ub.get(0).getBranchOffice().getEnterprise().getId());
        } else {
            log.info("Empresas usuario root");
            return enterpriseRepository.getEnterprisesListForRoot();
        }
    }
}
