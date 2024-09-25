package com.upb.cores.impl;


import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.BranchOfficeService;
import com.upb.cores.ProductService;
import com.upb.cores.UserService;
import com.upb.cores.WarehouseService;
import com.upb.cores.utils.NumberUtilMod;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.product.Product;
import com.upb.models.product.dto.ProductDto;
import com.upb.models.product.dto.ProductListDto;
import com.upb.models.warehouse.Warehouse;
import com.upb.models.warehouse.dto.WarehouseDto;
import com.upb.models.warehouse.dto.WarehousePagedDto;
import com.upb.models.warehouse.dto.WarehouseStateDto;
import com.upb.repositories.ProductRepository;
import com.upb.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final ProductService productService;
    private final BranchOfficeService branchOfficeService;

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<WarehousePagedDto> getWarehouseProductsList(String productName, String category, String maxOrMinLimit, Pageable pageable) {
        productName = (!StringUtil.isNullOrEmpty(productName) ? "%" +productName.toUpperCase() +"%" : null);
        /*BEBIDA, ALMUERZO, SANDWICH, EMPANADA*/
        category = (!StringUtil.isNullOrEmpty(category) ? "%"+ category.toUpperCase() +"%" : null);

        return warehouseRepository.getEnterprisePageable(productName, category, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse getWarehouseById(String idWarehouse) {
        return warehouseRepository.findWarehouseByIdAndStateNotDeleted(idWarehouse).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes al almacén")
        );
    }

    @Override
    @Transactional
    public WarehouseDto createWarehouse(String idProduct, String idBranchOffice, BigInteger stock, BigDecimal unitaryCost, BigInteger maxProduct, BigInteger minProduct) {
        Product product = productService.getProductById(idProduct);
        BranchOffice branchO = branchOfficeService.getBranchOfficeById(idBranchOffice);

        NumberUtilMod.throwNumberIsNullOrEmpty(stock, "stock");
        NumberUtilMod.throwNumberMaxDecimal(unitaryCost, 2,"precio unitario");
        NumberUtilMod.throwNumberIsNullOrEmpty(maxProduct, "máximo de producto");
        NumberUtilMod.throwNumberIsNullOrEmpty(minProduct, "mínimo de producto");

        Warehouse w = Warehouse.builder()
                .branchOffice(branchO)
                .product(product)
                .state("ACTIVE")
                .stock(stock)
                .unitaryCost(unitaryCost)
                .maxProduct(maxProduct)
                .minProduct(minProduct)
                .build();

        warehouseRepository.save(w);

        return new WarehouseDto(w);
    }

    @Override
    @Transactional
    public WarehouseDto updateWarehouse(String id, String idProduct, String idBranchOffice, BigInteger stock, BigDecimal unitaryCost, BigInteger maxProduct, BigInteger minProduct, String state) {
        Product product = productService.getProductById(idProduct);
        BranchOffice branchO = branchOfficeService.getBranchOfficeById(idBranchOffice);

        NumberUtilMod.throwNumberIsNullOrEmpty(stock, "stock");
        NumberUtilMod.throwNumberMaxDecimal(unitaryCost, 2,"precio unitario");
        NumberUtilMod.throwNumberIsNullOrEmpty(maxProduct, "máximo de producto");
        NumberUtilMod.throwNumberIsNullOrEmpty(minProduct, "mínimo de producto");
        StringUtilMod.throwStringIsNullOrEmpty(state, "estado");

        Warehouse w = warehouseRepository.findWarehouseByIdAndStateNotDeleted(id).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes al almacén")
        );

        w.setBranchOffice(branchO);
        w.setProduct(product);
        w.setState(state);
        w.setStock(stock);
        w.setMaxProduct(maxProduct);
        w.setUnitaryCost(unitaryCost);
        w.setMinProduct(minProduct);

        warehouseRepository.save(w);

        return new WarehouseDto(w);
    }

    @Override
    public WarehouseStateDto deleteWarehouseById(String id) {
        Warehouse w = warehouseRepository.findWarehouseByIdAndStateNotDeleted(id).orElseThrow(
                () -> new NoSuchElementException("No fue posible recuperar los valores correspondientes al almacén")
        );

        w.setState("DELETED");
        warehouseRepository.save(w);

        return new WarehouseStateDto(w);
    }
}
