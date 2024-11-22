package com.upb.cores.impl;


import com.upb.cores.BranchOfficeService;
import com.upb.cores.OrderDetailService;
import com.upb.cores.utils.StringUtilMod;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.branchOffice.dto.BranchOfficeStateDto;
import com.upb.models.detail.OrderDetail;
import com.upb.models.detail.dto.*;
import com.upb.models.document.Document;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.product.Product;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.models.warehouse.Warehouse;
import com.upb.repositories.OrderDetailRepository;
import com.upb.repositories.ProductRepository;
import com.upb.repositories.UserBranchOfficeRepository;
import com.upb.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserBranchOfficeRepository userBranchOfficeRepository;
    private final WarehouseRepository warehouseRepository;

    private final BranchOfficeService branchOfficeService;

    @Override
    public List<DetailCreatedDto> createDetail(Document document, List<DetailListRequest> detailList) {
        List<DetailCreatedDto> resp = new ArrayList<>();

        List<String> idList =detailList.stream().map(DetailListRequest::getIdProduct).toList();

        List<Product> productList = productRepository.getProductsListByIdList(idList);

        List< Warehouse> warehouseList = warehouseRepository.getWarehousesListByIdProductList(idList);

        if(idList.size() < productList.size()) {
            throw new NoSuchElementException("No fue posible recuperar los valores correspondientes al producto.");
        }
        Map<String, DetailListRequest> detailsByProductIdMap = detailList.stream()
                .collect(Collectors.toMap(DetailListRequest::getIdProduct, d -> d));

        Map<String, Warehouse> warehouseByProductIdMap = warehouseList.stream()
                .collect(Collectors.toMap(w -> w.getProduct().getId(), w -> w));

        productList.forEach(p -> {
            DetailListRequest d = detailsByProductIdMap.get(p.getId());
            Warehouse w = warehouseByProductIdMap.get(p.getId());

            if (d != null) {
                OrderDetail od = OrderDetail.builder()
                        .discount(d.getTotalDiscount())
                        .totalPrice(d.getTotalPrice())
                        .product(p)
                        .quantity(d.getQuantity())
                        .unitaryPrice(d.getUnitaryCost())
                        .document(document)
                        .sku(d.getSku())
                        .build();

                orderDetailRepository.save(od);

                resp.add(new DetailCreatedDto(od));

                if(w.getStock().compareTo(d.getQuantity()) < 0) {
                    throw new NoSuchElementException("El producto: " +p.getName()+ "de stock " +w.getStock()+ ", es inferior a la orden de " +d.getQuantity());
                }

                w.setStock(w.getStock().subtract(d.getQuantity()));
                warehouseRepository.save(w);
            }
        });

        return resp;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AllDetailInfoDto> getDetailDocumentInfo(String idDocument) {
        return orderDetailRepository.getDetailAndDocumentInfo(idDocument);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TotalCharInfoDto> charInfoDataList(Authentication auth, LocalDate date) {
        User user= (User) auth.getPrincipal();
        String idRol = auth.getAuthorities().stream().toList().get(0).toString();

        String idEnterprise = userBranchOfficeRepository.getUser_BranchOfficeByIdUserAndIdRol(user.getId(), idRol)
                .stream().map(User_BranchOffice::getBranchOffice).map(BranchOffice::getEnterprise)
                .map(Enterprise::getId).toList().get(0);

        List<String> idB = branchOfficeService.getBranchOfficeListByIdEnterprise(idEnterprise).stream()
                .map(BranchOfficeStateDto::getId).toList();

        Long first = date.atStartOfDay(ZoneId.of("America/La_Paz")).withDayOfMonth(1).toInstant().toEpochMilli();
        Long last = date.plusDays(1).atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();

        return orderDetailRepository.getCharInfoDataList(idB, first, last).stream()
                .collect(Collectors.groupingBy(data -> data.getBranchOffice().getId(),
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> {
                                    String branchOfficeName = list.get(0).getBranchOffice().getName();

                                    BigDecimal totalPrice = list.stream()
                                            .map(CharInfoDto::getDocument)
                                            .map(Document::getTotalPrice)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                                    BigInteger totalQuantity = list.stream()
                                            .map(CharInfoDto::getQuantity)
                                            .reduce(BigInteger.ZERO, BigInteger::add);

                                    return new TotalCharInfoDto(branchOfficeName, totalPrice, totalQuantity);
                                }
                        )
                ))
                .values()
                .stream()
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TotalCharProductInfoDto> charProductInfoDataList(String idBranchOffice, LocalDate date) {
        StringUtilMod.throwStringIsNullOrEmpty(idBranchOffice, "Id sucursal");

        Long first = date.atStartOfDay(ZoneId.of("America/La_Paz")).withDayOfMonth(1).toInstant().toEpochMilli();
        Long last = date.plusDays(1).atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();

        return orderDetailRepository.getCharProductInfoDataList(idBranchOffice, first, last).stream()
                .collect(Collectors.groupingBy(data -> data.getProduct().getId(),
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> {
                                    String branchOfficeName = list.get(0).getBranchOffice().getName();
                                    String productName = list.get(0).getProduct().getName();

                                    BigInteger totalQuantity = list.stream()
                                            .map(CharInfoDto::getQuantity)
                                            .reduce(BigInteger.ZERO, BigInteger::add);

                                    return new TotalCharProductInfoDto(branchOfficeName, productName, totalQuantity);
                                }
                        )
                ))
                .values()
                .stream()
                .toList();
    }
}

