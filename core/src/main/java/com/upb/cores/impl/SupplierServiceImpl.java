package com.upb.cores.impl;

import com.upb.cores.SupplierService;
import com.upb.models.supplier.Supplier;
import com.upb.repositories.SupplierRepository;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.user.User;
import com.upb.repositories.BranchOfficeRepository;
import com.upb.repositories.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final BranchOfficeRepository branchOfficeRepository;
    private final EnterpriseRepository enterpriseRepository;

    private Enterprise getEnterpriseFromAuth(Authentication auth) {
        User user = (User) auth.getPrincipal();
        String enterpriseId = branchOfficeRepository.findEnterpriseIdByUserId(user.getId());
        if (enterpriseId == null) {
            throw new NoSuchElementException("El usuario no tiene una empresa asignada");
        }
        return enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
    }

    @Override
    public List<Supplier> getAllSuppliers(Authentication auth) {
        Enterprise enterprise = getEnterpriseFromAuth(auth);
        return supplierRepository.findAllActiveByEnterpriseId(enterprise.getId());
    }

    @Override
    public Supplier createSupplier(String name, String contactName, String phone, Authentication auth) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("El nombre del proveedor es requerido.");

        Enterprise enterprise = getEnterpriseFromAuth(auth);

        Supplier supplier = Supplier.builder()
                .name(name)
                .contactName(contactName)
                .phone(phone)
                .state("ACTIVE")
                .enterprise(enterprise)
                .build();

        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier updateSupplier(String id, String name, String contactName, String phone, String state) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado con ID: " + id));

        if (name != null && !name.isBlank()) supplier.setName(name);
        if (contactName != null) supplier.setContactName(contactName);
        if (phone != null) supplier.setPhone(phone);
        if (state != null) supplier.setState(state);

        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier deleteSupplier(String id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado con ID: " + id));

        supplier.setState("DELETED");
        return supplierRepository.save(supplier);
    }
}
