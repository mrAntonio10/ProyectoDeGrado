package com.upb.cores;

import com.upb.models.transferTicket.TransferTicket;
import com.upb.models.transferTicket.dto.DetailUpdateDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransferTicketService {

    List<TransferTicket> getTickets(String branchId, Authentication auth);

    TransferTicket getTicketById(String id);

    TransferTicket createTicket(String transferType, String sourceId, String destinationId,
                                 String comments, List<String> productIds, List<Integer> quantities, Authentication auth);

    /**
     * Registra un evento de auditoría INMUTABLE y cambia el estado del ticket.
     * @param ticketId      ID del ticket
     * @param newStatus     ACCEPTED | REJECTED | PARTIALLY_DAMAGED
     * @param comments      Observación obligatoria para REJECTED
     * @param detailUpdates Lista opcional de cantidades recibidas/dañadas por producto
     * @param auth          AuthResource del usuario que realiza la acción
     */
    TransferTicket updateTicketStatus(String ticketId, String newStatus, String comments,
                                      List<DetailUpdateDto> detailUpdates,
                                      Authentication auth);
}

