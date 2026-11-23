package com.upb.cores;

import com.upb.models.event.StoreEvent;
import com.upb.models.event.dto.EventProductDto;
import com.upb.models.event.dto.StoreEventDto;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

public interface StoreEventService {

    void ensureDefaultEventsExist();

    List<StoreEventDto> getAllEvents(String region);

    StoreEventDto getEventByIdOrCode(String idOrCode, String region);

    StoreEventDto createEvent(Authentication auth,
                              String name,
                              String description,
                              Long startDate,
                              Long endDate,
                              List<String> productIds,
                              String discountType,
                              BigDecimal discountValue);

    StoreEventDto updateEvent(Authentication auth,
                              String id,
                              String name,
                              String description,
                              Long startDate,
                              Long endDate,
                              List<String> productIds,
                              String discountType,
                              BigDecimal discountValue);

    StoreEventDto deleteEvent(String id);

    StoreEventDto assignProductsToEvent(Authentication auth,
                                        String eventId,
                                        List<String> productIds,
                                        String discountType,
                                        BigDecimal discountValue);

    StoreEventDto removeProductFromEvent(String eventId, String productId);

    List<EventProductDto> applyAdminDiscount(Authentication auth,
                                             List<String> productIds,
                                             String discountType,
                                             BigDecimal discountValue);

    void removeAdminDiscount(Authentication auth, String productId);

    StoreEvent getOrCreateNovedadesEvent();

    StoreEvent getOrCreateDescuentosEvent();
}
