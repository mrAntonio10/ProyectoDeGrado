package com.upb.repositories;

import com.upb.models.transferTicket.TransferTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferTicketRepository extends JpaRepository<TransferTicket, String> {

    // Todos los tickets donde este branchOffice es destino
    @Query("SELECT t FROM TransferTicket t LEFT JOIN FETCH t.destination LEFT JOIN FETCH t.details d LEFT JOIN FETCH d.product LEFT JOIN FETCH t.events e LEFT JOIN FETCH e.user WHERE t.destination.id = :branchId ORDER BY t.dateCreated DESC")
    List<TransferTicket> findByDestinationId(@Param("branchId") String branchId);

    // Todos los tickets donde este branchOffice es origen (transferencias internas)
    @Query("SELECT t FROM TransferTicket t LEFT JOIN FETCH t.destination LEFT JOIN FETCH t.details d LEFT JOIN FETCH d.product LEFT JOIN FETCH t.events e LEFT JOIN FETCH e.user WHERE t.sourceId = :sourceId ORDER BY t.dateCreated DESC")
    List<TransferTicket> findBySourceId(@Param("sourceId") String sourceId);

    @Query("SELECT t FROM TransferTicket t LEFT JOIN FETCH t.destination LEFT JOIN FETCH t.details d LEFT JOIN FETCH d.product LEFT JOIN FETCH t.events e LEFT JOIN FETCH e.user WHERE t.id = :id")
    Optional<TransferTicket> findByIdWithDetails(@Param("id") String id);

    @Query("SELECT t FROM TransferTicket t LEFT JOIN FETCH t.destination LEFT JOIN FETCH t.details d LEFT JOIN FETCH d.product LEFT JOIN FETCH t.events e LEFT JOIN FETCH e.user WHERE t.enterprise.id = :enterpriseId ORDER BY t.dateCreated DESC")
    List<TransferTicket> findAllByEnterpriseId(@Param("enterpriseId") String enterpriseId);
}
