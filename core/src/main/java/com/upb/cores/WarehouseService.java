package com.upb.cores;

import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductDto;
import com.upb.models.product.dto.ProductListDto;
import com.upb.models.warehouse.Warehouse;
import com.upb.models.warehouse.dto.WarehouseDto;
import com.upb.models.warehouse.dto.WarehousePagedDto;
import com.upb.models.warehouse.dto.WarehouseStateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
public interface WarehouseService {
    Page<WarehousePagedDto> getWarehouseProductsList(String productName, String category, String maxOrMinLimit, Pageable pageable);
    Warehouse getWarehouseById(String idWarehouse);
    WarehouseDto createWarehouse(String idProduct, String idBranchOffice, BigInteger stock, BigDecimal unitaryCost, BigInteger maxProduct, BigInteger minProduct);
    WarehouseDto updateWarehouse(String id, String idProduct, String idBranchOffice, BigInteger stock, BigDecimal unitaryCost, BigInteger maxProduct, BigInteger minProduct, String state);
    WarehouseStateDto deleteWarehouseById(String id);
}
