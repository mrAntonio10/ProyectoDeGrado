package com.upb.repositories;

import com.upb.models.event.StoreEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreEventRepository extends JpaRepository<StoreEvent, String> {

    @Query("SELECT e FROM StoreEvent e WHERE e.state <> 'DELETED' ORDER BY e.isDefault DESC, e.name ASC")
    List<StoreEvent> findAllActiveEvents();

    @Query("SELECT e FROM StoreEvent e WHERE UPPER(e.code) = UPPER(:code) AND e.state <> 'DELETED'")
    Optional<StoreEvent> findActiveByCode(@Param("code") String code);

    @Query("SELECT e FROM StoreEvent e WHERE e.id = :id AND e.state <> 'DELETED'")
    Optional<StoreEvent> findActiveById(@Param("id") String id);
}
