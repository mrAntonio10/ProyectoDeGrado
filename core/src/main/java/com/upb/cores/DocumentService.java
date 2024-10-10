package com.upb.cores;

import com.upb.models.document.dto.DocumentCreatedDto;
import com.upb.models.warehouse.Warehouse;
import com.upb.models.warehouse.dto.WarehouseDto;
import com.upb.models.warehouse.dto.WarehousePagedDto;
import com.upb.models.warehouse.dto.WarehouseStateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public interface DocumentService {
//    Page<WarehousePagedDto> getWarehouseProductsList(Authentication auth, String productName, String idBranchOffice, String category, String maxOrMinLimit, Pageable pageable);
//    Warehouse getWarehouseById(String idWarehouse);
    DocumentCreatedDto createDetailDocument(String idProduct, String idBranchOffice, BigInteger stock, BigDecimal unitaryCost, BigInteger maxProduct, BigInteger minProduct);
//    WarehouseDto updateWarehouse(String id, String idProduct, String idBranchOffice, BigInteger stock, BigDecimal unitaryCost, BigInteger maxProduct, BigInteger minProduct);
//    WarehouseStateDto deleteWarehouseById(String id);
}
