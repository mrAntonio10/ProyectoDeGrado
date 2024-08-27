package com.upb.cores.impl;


import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.EnterpriseService;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.enterprise.dto.EnterpriseDto;
import com.upb.models.enterprise.dto.EnterpriseStateDto;
import com.upb.repositories.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnterpriseServiceImpl implements EnterpriseService {
    private final EnterpriseRepository enterpriseRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<EnterpriseDto> getEnterprisePageable(String name, Pageable pageable) {
       name = (!StringUtil.isNullOrEmpty(name) ? "%" +name.toUpperCase()+ "%" : null);

        return enterpriseRepository.getEnterprisePageable(name, pageable);
    }

    @Override
    public EnterpriseDto createEnterprise(String name, String email, String phoneNumber, String logo, String description) {
        StringUtilMod.throwStringIsNullOrEmpty(name, "nombre");
        StringUtilMod.throwStringIsNullOrEmpty(email, "email");
        StringUtilMod.throwStringIsNullOrEmpty(description, "descripción");

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
        StringUtilMod.throwStringIsNullOrEmpty(name, "nombre");
        StringUtilMod.throwStringIsNullOrEmpty(email, "email");
        StringUtilMod.throwStringIsNullOrEmpty(description, "descripción");

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
}
