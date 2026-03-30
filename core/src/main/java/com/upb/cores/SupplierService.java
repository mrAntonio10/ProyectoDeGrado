package com.upb.cores;

import com.upb.models.supplier.Supplier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SupplierService {
    List<Supplier> getAllSuppliers(Authentication auth);
    Supplier createSupplier(String name, String contactName, String phone, Authentication auth);
    Supplier updateSupplier(String id, String name, String contactName, String phone, String state);
    Supplier deleteSupplier(String id);
}
