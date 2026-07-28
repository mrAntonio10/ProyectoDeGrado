package com.upb.cores;

import org.springframework.security.core.Authentication;

import java.util.List;

public interface DomainService {
    Long checkDomainValueUsage(String domainId);

    // --- Admin Domain Endpoints ---
    List<String> getMasterDomains(Authentication auth);

    List<com.upb.models.utils.dto.DomainAdminDto> getDomainValues(Authentication auth, String domainName);

    com.upb.models.utils.dto.DomainAdminDto createDomainValue(Authentication auth, String domainName, String valueName, String description);

    com.upb.models.utils.dto.DomainAdminDto updateDomainValue(Authentication auth, String id, String valueName, String description);

    void deleteDomainValue(Authentication auth, String id);
}
