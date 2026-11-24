package com.upb.repositories;

import com.upb.models.event.EventProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventProductRepository extends JpaRepository<EventProduct, String> {

    @Query("SELECT ep FROM EventProduct ep " +
            "INNER JOIN FETCH ep.event e " +
            "INNER JOIN FETCH ep.product p " +
            "WHERE ep.state = true AND e.state <> 'DELETED' AND p.state <> false " +
            "AND e.id = :eventId " +
            "AND (:region = '' OR UPPER(COALESCE(p.region, '')) = :region)")
    List<EventProduct> findActiveByEventIdAndRegion(@Param("eventId") String eventId,
                                                    @Param("region") String region);

    @Query("SELECT ep FROM EventProduct ep " +
            "INNER JOIN FETCH ep.event e " +
            "INNER JOIN FETCH ep.product p " +
            "WHERE ep.state = true AND e.state <> 'DELETED' AND p.state <> false " +
            "AND p.id = :productId")
    List<EventProduct> findActiveByProductId(@Param("productId") String productId);

    @Query("SELECT ep FROM EventProduct ep " +
            "WHERE ep.event.id = :eventId AND ep.product.id = :productId")
    Optional<EventProduct> findByEventIdAndProductId(@Param("eventId") String eventId,
                                                     @Param("productId") String productId);
}
