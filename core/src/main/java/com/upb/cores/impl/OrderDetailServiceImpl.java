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
    @Transactional
    public List<DetailCreatedDto> createDetail(Document document, List<DetailListRequest> detailList) {
        List<DetailCreatedDto> resp = new ArrayList<>();
        if (detailList == null || detailList.isEmpty()) {
            return resp;
        }

        for (DetailListRequest d : detailList) {
            Product p = null;
            if (d.getIdProduct() != null && !d.getIdProduct().isBlank()) {
                p = productRepository.findProductByIdAndStateTrue(d.getIdProduct()).orElse(null);
            }
            if (p == null && d.getSku() != null && !d.getSku().isBlank()) {
                p = productRepository.findBySkuAndRegion(d.getSku(), document.getRegion()).orElse(null);
                if (p == null) {
                    p = productRepository.findBySkuAndRegion(d.getSku(), null).orElse(null);
                }
            }
            if (p == null && d.getIdProduct() != null && !d.getIdProduct().isBlank()) {
                // Intentar buscar por SKU si idProduct contiene el código de la prenda
                p = productRepository.findBySkuAndRegion(d.getIdProduct(), null).orElse(null);
            }

            if (p == null) {
                throw new NoSuchElementException("No fue posible encontrar la prenda con código/ID: "
                        + (d.getSku() != null ? d.getSku() : d.getIdProduct()));
            }

            BigInteger qty = d.getQuantity() != null ? d.getQuantity() : BigInteger.ONE;
            BigDecimal unitPrice = d.getUnitaryCost() != null ? d.getUnitaryCost()
                    : (p.getFinalPrice() != null ? p.getFinalPrice() : p.getSalePrice());
            BigDecimal discount = d.getTotalDiscount() != null ? d.getTotalDiscount() : BigDecimal.ZERO;
            BigDecimal total = d.getTotalPrice() != null ? d.getTotalPrice()
                    : unitPrice.multiply(new BigDecimal(qty)).subtract(discount);

            OrderDetail od = OrderDetail.builder()
                    .discount(discount)
                    .totalPrice(total)
                    .product(p)
                    .quantity(qty)
                    .unitaryPrice(unitPrice)
                    .document(document)
                    .sku(d.getSku() != null ? d.getSku() : p.getSku())
                    .build();

            orderDetailRepository.save(od);
            resp.add(new DetailCreatedDto(od));

            List<Warehouse> warehouseList = warehouseRepository.getWarehousesListByIdProductList(List.of(p.getId()));
            if (!warehouseList.isEmpty()) {
                Warehouse w = warehouseList.get(0);
                if (w.getStock() != null) {
                    if (w.getStock().compareTo(qty) < 0) {
                        throw new NoSuchElementException("La prenda: " + p.getName() + " de stock " + w.getStock() + ", es inferior a la orden de " + qty);
                    }
                    w.setStock(w.getStock().subtract(qty));
                    warehouseRepository.save(w);
                }
            }
        }

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

        List<CharInfoDto> resp = orderDetailRepository.getCharInfoDataList(idB, first, last);

        return resp.stream()
                .collect(Collectors.groupingBy(data -> data.getBranchOffice().getId(),
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> {
                                    String branchOfficeName = list.get(0).getBranchOffice().getName();

                                    BigDecimal totalPrice = list.stream()
                                            .map(CharInfoDto::getSellPrice)
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

