package com.upb.cores.impl;

import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.StoreEventService;
import com.upb.models.event.EventProduct;
import com.upb.models.event.StoreEvent;
import com.upb.models.event.dto.EventProductDto;
import com.upb.models.event.dto.StoreEventDto;
import com.upb.models.product.Product;
import com.upb.models.user.User;
import com.upb.repositories.EventProductRepository;
import com.upb.repositories.ProductRepository;
import com.upb.repositories.StoreEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreEventServiceImpl implements StoreEventService {

    private final StoreEventRepository storeEventRepository;
    private final EventProductRepository eventProductRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void ensureDefaultEventsExist() {
        getOrCreateNovedadesEvent();
        getOrCreateDescuentosEvent();
    }

    @Override
    @Transactional
    public StoreEvent getOrCreateNovedadesEvent() {
        Optional<StoreEvent> opt = storeEventRepository.findActiveByCode("NOVEDADES");
        if (opt.isPresent()) {
            return opt.get();
        }
        StoreEvent novedades = StoreEvent.builder()
                .name("Novedades")
                .code("NOVEDADES")
                .description("Todas las prendas nuevas recién ingresadas o sincronizadas en Rekids")
                .isDefault(true)
                .discountType("NONE")
                .discountValue(BigDecimal.ZERO)
                .state("ACTIVE")
                .build();
        return storeEventRepository.save(novedades);
    }

    @Override
    @Transactional
    public StoreEvent getOrCreateDescuentosEvent() {
        Optional<StoreEvent> opt = storeEventRepository.findActiveByCode("DESCUENTOS");
        if (opt.isPresent()) {
            return opt.get();
        }
        StoreEvent descuentos = StoreEvent.builder()
                .name("Descuentos")
                .code("DESCUENTOS")
                .description("Prendas con descuento fijo o porcentual aprobado por un administrador")
                .isDefault(true)
                .discountType("PORCENTUAL")
                .discountValue(BigDecimal.ZERO)
                .state("ACTIVE")
                .build();
        return storeEventRepository.save(descuentos);
    }

    @Override
    @Transactional
    public List<StoreEventDto> getAllEvents(String region) {
        ensureDefaultEventsExist();
        String normRegion = (!StringUtil.isNullOrEmpty(region) && !region.equalsIgnoreCase("TODOS")) ? region.trim().toUpperCase() : "";

        List<StoreEvent> events = storeEventRepository.findAllActiveEvents();
        return events.stream()
                .map(ev -> buildStoreEventDto(ev, normRegion))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StoreEventDto getEventByIdOrCode(String idOrCode, String region) {
        ensureDefaultEventsExist();
        String normRegion = (!StringUtil.isNullOrEmpty(region) && !region.equalsIgnoreCase("TODOS")) ? region.trim().toUpperCase() : "";

        StoreEvent event = storeEventRepository.findActiveById(idOrCode)
                .or(() -> storeEventRepository.findActiveByCode(idOrCode))
                .orElseThrow(() -> new NoSuchElementException("No se encontró el evento con ID o código: " + idOrCode));

        return buildStoreEventDto(event, normRegion);
    }

    private StoreEventDto buildStoreEventDto(StoreEvent event, String region) {
        String safeRegion = (region != null && !region.equalsIgnoreCase("TODOS")) ? region.trim().toUpperCase() : "";
        List<EventProduct> eventProducts = eventProductRepository.findActiveByEventIdAndRegion(event.getId(), safeRegion);

        // Si es el evento NOVEDADES, aseguramos que todas las prendas marcadas como nuevas estén incluidas
        if ("NOVEDADES".equalsIgnoreCase(event.getCode())) {
            Set<String> includedIds = eventProducts.stream()
                    .map(ep -> ep.getProduct().getId())
                    .collect(Collectors.toSet());
            List<Product> allRegionProducts = productRepository.findAllActiveByRegion(safeRegion);
            for (Product p : allRegionProducts) {
                if (Boolean.TRUE.equals(p.getIsNew()) && !includedIds.contains(p.getId())) {
                    EventProduct ep = EventProduct.builder()
                            .event(event)
                            .product(p)
                            .originalPrice(p.getSalePrice())
                            .finalPrice(p.getFinalPrice() != null ? p.getFinalPrice() : p.getSalePrice())
                            .discountType(p.getDiscountType() != null ? p.getDiscountType() : "NONE")
                            .discountValue(p.getDiscountValue() != null ? p.getDiscountValue() : BigDecimal.ZERO)
                            .state(true)
                            .build();
                    ep = eventProductRepository.save(ep);
                    eventProducts.add(ep);
                }
            }
        }

        List<EventProductDto> dtos = eventProducts.stream()
                .map(EventProductDto::new)
                .collect(Collectors.toList());

        return new StoreEventDto(event, dtos);
    }

    @Override
    @Transactional
    public StoreEventDto createEvent(Authentication auth,
                                     String name,
                                     String description,
                                     Long startDate,
                                     Long endDate,
                                     List<String> productIds,
                                     String discountType,
                                     BigDecimal discountValue) {
        if (StringUtil.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("El nombre del evento es obligatorio.");
        }

        String code = name.trim().toUpperCase().replaceAll("\\s+", "_");
        if (storeEventRepository.findActiveByCode(code).isPresent()) {
            throw new IllegalArgumentException("Ya existe un evento activo con el nombre/código: " + name);
        }

        AdminAuditInfo adminInfo = extractAdminInfo(auth);
        String normDiscountType = normalizeDiscountType(discountType);
        BigDecimal normDiscountVal = discountValue != null ? discountValue : BigDecimal.ZERO;

        StoreEvent event = StoreEvent.builder()
                .name(name.trim())
                .code(code)
                .description(description)
                .isDefault(false)
                .discountType(normDiscountType)
                .discountValue(normDiscountVal)
                .approvedByAdminId(adminInfo.id)
                .approvedByAdminName(adminInfo.name)
                .approvedDate(System.currentTimeMillis())
                .startDate(startDate)
                .endDate(endDate)
                .state("ACTIVE")
                .build();

        event = storeEventRepository.save(event);

        if (productIds != null && !productIds.isEmpty()) {
            assignProductsInternal(event, productIds, normDiscountType, normDiscountVal, adminInfo);
        }

        return buildStoreEventDto(event, null);
    }

    @Override
    @Transactional
    public StoreEventDto updateEvent(Authentication auth,
                                     String id,
                                     String name,
                                     String description,
                                     Long startDate,
                                     Long endDate,
                                     List<String> productIds,
                                     String discountType,
                                     BigDecimal discountValue) {
        StoreEvent event = storeEventRepository.findActiveById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el evento con ID: " + id));

        if (!Boolean.TRUE.equals(event.getIsDefault()) && !StringUtil.isNullOrEmpty(name)) {
            event.setName(name.trim());
            event.setCode(name.trim().toUpperCase().replaceAll("\\s+", "_"));
        }
        if (description != null) {
            event.setDescription(description);
        }
        event.setStartDate(startDate);
        event.setEndDate(endDate);

        AdminAuditInfo adminInfo = extractAdminInfo(auth);
        String normDiscountType = normalizeDiscountType(discountType != null ? discountType : event.getDiscountType());
        BigDecimal normDiscountVal = discountValue != null ? discountValue : (event.getDiscountValue() != null ? event.getDiscountValue() : BigDecimal.ZERO);

        event.setDiscountType(normDiscountType);
        event.setDiscountValue(normDiscountVal);
        event.setApprovedByAdminId(adminInfo.id);
        event.setApprovedByAdminName(adminInfo.name);
        event.setApprovedDate(System.currentTimeMillis());

        storeEventRepository.save(event);

        if (productIds != null) {
            // Desactivar los que no estén en la nueva lista
            List<EventProduct> current = eventProductRepository.findActiveByEventIdAndRegion(event.getId(), "");
            Set<String> targetIds = new HashSet<>(productIds);
            for (EventProduct ep : current) {
                if (!targetIds.contains(ep.getProduct().getId())) {
                    ep.setState(false);
                    eventProductRepository.save(ep);
                }
            }
            assignProductsInternal(event, productIds, normDiscountType, normDiscountVal, adminInfo);
        }

        return buildStoreEventDto(event, null);
    }

    @Override
    @Transactional
    public StoreEventDto deleteEvent(String id) {
        StoreEvent event = storeEventRepository.findActiveById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el evento con ID: " + id));

        if (Boolean.TRUE.equals(event.getIsDefault())
                || "NOVEDADES".equalsIgnoreCase(event.getCode())
                || "DESCUENTOS".equalsIgnoreCase(event.getCode())) {
            throw new IllegalArgumentException("Los eventos permanentes 'Novedades' y 'Descuentos' siempre deben estar activos y no pueden eliminarse.");
        }

        event.setState("DELETED");
        storeEventRepository.save(event);

        List<EventProduct> current = eventProductRepository.findActiveByEventIdAndRegion(event.getId(), "");
        for (EventProduct ep : current) {
            ep.setState(false);
            eventProductRepository.save(ep);
        }

        return new StoreEventDto(event, Collections.emptyList());
    }

    @Override
    @Transactional
    public StoreEventDto assignProductsToEvent(Authentication auth,
                                               String eventId,
                                               List<String> productIds,
                                               String discountType,
                                               BigDecimal discountValue) {
        StoreEvent event = storeEventRepository.findActiveById(eventId)
                .or(() -> storeEventRepository.findActiveByCode(eventId))
                .orElseThrow(() -> new NoSuchElementException("No se encontró el evento con ID: " + eventId));

        AdminAuditInfo adminInfo = extractAdminInfo(auth);
        String normDiscountType = normalizeDiscountType(discountType != null ? discountType : event.getDiscountType());
        BigDecimal normDiscountVal = discountValue != null ? discountValue : (event.getDiscountValue() != null ? event.getDiscountValue() : BigDecimal.ZERO);

        assignProductsInternal(event, productIds, normDiscountType, normDiscountVal, adminInfo);

        return buildStoreEventDto(event, null);
    }

    @Override
    @Transactional
    public StoreEventDto removeProductFromEvent(String eventId, String productId) {
        StoreEvent event = storeEventRepository.findActiveById(eventId)
                .or(() -> storeEventRepository.findActiveByCode(eventId))
                .orElseThrow(() -> new NoSuchElementException("No se encontró el evento con ID: " + eventId));

        Optional<EventProduct> epOpt = eventProductRepository.findByEventIdAndProductId(event.getId(), productId);
        if (epOpt.isPresent()) {
            EventProduct ep = epOpt.get();
            ep.setState(false);
            eventProductRepository.save(ep);
        }

        if ("DESCUENTOS".equalsIgnoreCase(event.getCode())) {
            productRepository.findProductByIdAndStateTrue(productId).ifPresent(p -> {
                p.setDiscountType("NONE");
                p.setDiscountValue(BigDecimal.ZERO);
                p.setFinalPrice(p.getSalePrice());
                p.setDiscountApprovedBy(null);
                p.setDiscountApprovedDate(null);
                productRepository.save(p);
            });
        }

        return buildStoreEventDto(event, null);
    }

    @Override
    @Transactional
    public List<EventProductDto> applyAdminDiscount(Authentication auth,
                                                    List<String> productIds,
                                                    String discountType,
                                                    BigDecimal discountValue) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una prenda para aplicar el descuento.");
        }
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor del descuento debe ser mayor a 0.");
        }

        String normType = normalizeDiscountType(discountType);
        if (!"MONTO_FIJO".equals(normType) && !"PORCENTUAL".equals(normType)) {
            throw new IllegalArgumentException("El tipo de descuento debe ser MONTO_FIJO o PORCENTUAL.");
        }
        if ("PORCENTUAL".equals(normType) && discountValue.compareTo(new BigDecimal("100")) >= 0) {
            throw new IllegalArgumentException("El descuento porcentual debe ser menor al 100%.");
        }

        AdminAuditInfo adminInfo = extractAdminInfo(auth);
        StoreEvent descuentosEvent = getOrCreateDescuentosEvent();

        descuentosEvent.setDiscountType(normType);
        descuentosEvent.setDiscountValue(discountValue);
        descuentosEvent.setApprovedByAdminId(adminInfo.id);
        descuentosEvent.setApprovedByAdminName(adminInfo.name);
        descuentosEvent.setApprovedDate(System.currentTimeMillis());
        storeEventRepository.save(descuentosEvent);

        List<EventProduct> savedList = assignProductsInternal(descuentosEvent, productIds, normType, discountValue, adminInfo);
        return savedList.stream().map(EventProductDto::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeAdminDiscount(Authentication auth, String productId) {
        StoreEvent descuentosEvent = getOrCreateDescuentosEvent();
        removeProductFromEvent(descuentosEvent.getId(), productId);
    }

    private List<EventProduct> assignProductsInternal(StoreEvent event,
                                                      List<String> productIds,
                                                      String discountType,
                                                      BigDecimal discountValue,
                                                      AdminAuditInfo adminInfo) {
        List<EventProduct> result = new ArrayList<>();
        if (productIds == null || productIds.isEmpty()) {
            return result;
        }

        List<Product> products = productRepository.getProductsListByIdList(productIds);
        long now = System.currentTimeMillis();
        boolean isDiscountEvent = "DESCUENTOS".equalsIgnoreCase(event.getCode())
                || ("MONTO_FIJO".equals(discountType) || "PORCENTUAL".equals(discountType))
                && discountValue != null && discountValue.compareTo(BigDecimal.ZERO) > 0;

        for (Product p : products) {
            BigDecimal basePrice = p.getSalePrice() != null ? p.getSalePrice() : BigDecimal.ZERO;
            BigDecimal finalPrice = calculateFinalPrice(basePrice, discountType, discountValue);

            if (isDiscountEvent) {
                p.setDiscountType(discountType);
                p.setDiscountValue(discountValue);
                p.setFinalPrice(finalPrice);
                p.setDiscountApprovedBy(adminInfo.name);
                p.setDiscountApprovedDate(now);
                productRepository.save(p);
            }

            EventProduct ep = eventProductRepository.findByEventIdAndProductId(event.getId(), p.getId())
                    .orElseGet(() -> EventProduct.builder()
                            .event(event)
                            .product(p)
                            .build());

            ep.setOriginalPrice(basePrice);
            ep.setDiscountType(isDiscountEvent ? discountType : (p.getDiscountType() != null ? p.getDiscountType() : "NONE"));
            ep.setDiscountValue(isDiscountEvent ? discountValue : (p.getDiscountValue() != null ? p.getDiscountValue() : BigDecimal.ZERO));
            ep.setFinalPrice(isDiscountEvent ? finalPrice : (p.getFinalPrice() != null ? p.getFinalPrice() : basePrice));
            ep.setApprovedByAdminId(adminInfo.id);
            ep.setApprovedByAdminName(adminInfo.name);
            ep.setApprovedDate(now);
            ep.setState(true);

            result.add(eventProductRepository.save(ep));
        }

        return result;
    }

    private BigDecimal calculateFinalPrice(BigDecimal originalPrice, String discountType, BigDecimal discountValue) {
        if (originalPrice == null) {
            return BigDecimal.ZERO;
        }
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0 || discountType == null) {
            return originalPrice;
        }
        String norm = normalizeDiscountType(discountType);
        BigDecimal finalPrice = originalPrice;
        if ("MONTO_FIJO".equals(norm)) {
            finalPrice = originalPrice.subtract(discountValue);
        } else if ("PORCENTUAL".equals(norm)) {
            BigDecimal discountAmount = originalPrice.multiply(discountValue)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            finalPrice = originalPrice.subtract(discountAmount);
        }
        return finalPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeDiscountType(String discountType) {
        if (StringUtil.isNullOrEmpty(discountType)) {
            return "NONE";
        }
        String upper = discountType.trim().toUpperCase();
        if (upper.contains("FIJO") || upper.equals("FIXED")) {
            return "MONTO_FIJO";
        }
        if (upper.contains("PORCENT") || upper.equals("PERCENTAGE") || upper.equals("%")) {
            return "PORCENTUAL";
        }
        return "NONE";
    }

    private AdminAuditInfo extractAdminInfo(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof User user) {
            String fullName = ((user.getName() != null ? user.getName() : "") + " "
                    + (user.getLastname() != null ? user.getLastname() : "")).trim();
            String label = fullName.isEmpty() ? user.getEmail() : fullName + " (" + user.getEmail() + ")";
            return new AdminAuditInfo(user.getId(), label);
        }
        return new AdminAuditInfo("ADMIN", "Administrador Rekids (admin@gmail.com)");
    }

    private record AdminAuditInfo(String id, String name) {}
}
