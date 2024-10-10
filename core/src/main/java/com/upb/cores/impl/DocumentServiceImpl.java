package com.upb.cores.impl;


import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.BranchOfficeService;
import com.upb.cores.DocumentService;
import com.upb.cores.ProductService;
import com.upb.cores.WarehouseService;
import com.upb.cores.utils.NumberUtilMod;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.document.dto.DocumentCreatedDto;
import com.upb.models.product.Product;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.models.warehouse.Warehouse;
import com.upb.models.warehouse.dto.WarehouseDto;
import com.upb.models.warehouse.dto.WarehousePagedDto;
import com.upb.models.warehouse.dto.WarehouseStateDto;
import com.upb.repositories.UserBranchOfficeRepository;
import com.upb.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {


    @Override
    public DocumentCreatedDto createDetailDocument(String idProduct, String idBranchOffice, BigInteger stock, BigDecimal unitaryCost, BigInteger maxProduct, BigInteger minProduct) {
        return null;
    }
}
