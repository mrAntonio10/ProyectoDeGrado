package com.upb.cores.impl;

import com.upb.cores.TransferTicketService;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.enterprise.Enterprise;
import com.upb.models.product.Product;
import com.upb.models.transferTicket.TicketEvent;
import com.upb.models.transferTicket.TransferDetail;
import com.upb.models.transferTicket.TransferTicket;
import com.upb.models.transferTicket.dto.DetailUpdateDto;
import com.upb.models.user.User;
import com.upb.models.warehouse.Warehouse;
import com.upb.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferTicketServiceImpl implements TransferTicketService {

    private final TransferTicketRepository transferTicketRepository;
    private final BranchOfficeRepository branchOfficeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final WarehouseRepository warehouseRepository;
    
    // Método para extraer la empresa actual
    private Enterprise getEnterpriseFromAuth(Authentication auth) {
        User user = (User) auth.getPrincipal();
        String enterpriseId = branchOfficeRepository.findEnterpriseIdByUserId(user.getId());
        if (enterpriseId == null) {
            throw new NoSuchElementException("El usuario no tiene una empresa asignada");
        }
        return enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
    }

    @Override
    public List<TransferTicket> getTickets(String branchId, Authentication auth) {
        if (branchId != null && !branchId.isEmpty()) {
            return transferTicketRepository.findByDestinationId(branchId);
        } else {
            Enterprise enterprise = getEnterpriseFromAuth(auth);
            return transferTicketRepository.findAllByEnterpriseId(enterprise.getId());
        }
    }

    @Override
    public TransferTicket getTicketById(String id) {
        return transferTicketRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket no encontrado: " + id));
    }

    @Override
    @Transactional
    public TransferTicket createTicket(String transferType, String sourceId, String destinationId,
                                        String comments, List<String> productIds, List<Integer> quantities, Authentication auth) {

        Enterprise enterprise = getEnterpriseFromAuth(auth);

        BranchOffice destination = branchOfficeRepository.findById(destinationId)
                .orElseThrow(() -> new NoSuchElementException("Almacén destino no encontrado: " + destinationId));

        TransferTicket ticket = TransferTicket.builder()
                .transferType(transferType)
                .sourceId(sourceId)
                .destination(destination)
                .currentStatus("PENDING")
                .dateCreated(LocalDateTime.now())
                .comments(comments)
                .enterprise(enterprise)
                .details(new LinkedHashSet<>())
                .events(new LinkedHashSet<>())
                .build();

        ticket = transferTicketRepository.save(ticket);

        // Crear los detalles de productos y restar de almacén si es INTERNAL_TRANSFER
        boolean isInternal = "INTERNAL_TRANSFER".equals(transferType);

        for (int i = 0; i < productIds.size(); i++) {
            String productId = productIds.get(i);
            int quantity = quantities.get(i);
            
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + productId));

            if (isInternal) {
                // Restar stock de origen
                Warehouse sourceWarehouse = warehouseRepository.findByBranchOfficeIdAndProductId(sourceId, productId)
                        .orElseThrow(() -> new NoSuchElementException("El producto " + product.getName() + " no está en el almacén de origen."));

                BigInteger remaining = sourceWarehouse.getStock().subtract(BigInteger.valueOf(quantity));
                if (remaining.compareTo(BigInteger.ZERO) < 0) {
                    throw new IllegalArgumentException("Stock insuficiente para: " + product.getName() + " (Disp: " + sourceWarehouse.getStock() + ")");
                }
                sourceWarehouse.setStock(remaining);
                warehouseRepository.save(sourceWarehouse);
            }

            TransferDetail detail = TransferDetail.builder()
                    .ticket(ticket)
                    .product(product)
                    .quantityShipped(BigInteger.valueOf(quantity))
                    .quantityReceived(BigInteger.ZERO)
                    .quantityDamaged(BigInteger.ZERO)
                    .build();

            ticket.getDetails().add(detail);
        }

        return transferTicketRepository.save(ticket);
    }

    @Override
    @Transactional
    public TransferTicket updateTicketStatus(String ticketId, String newStatus, String comments,
                                             List<DetailUpdateDto> detailUpdates,
                                             Authentication auth) {

        TransferTicket ticket = transferTicketRepository.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Ticket no encontrado: " + ticketId));

        User user = userRepository.findByEmailAndStateActive(auth.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        ticket.setCurrentStatus(newStatus);

        TicketEvent event = TicketEvent.builder()
                .ticket(ticket)
                .statusChange(newStatus)
                .user(user)
                .date(LocalDateTime.now())
                .comments(comments)
                .build();

        if (ticket.getEvents() == null) ticket.setEvents(new LinkedHashSet<>());
        ticket.getEvents().add(event);

        // Construir mapa de actualizaciones por productId para acceso rápido
        Map<String, DetailUpdateDto> updateMap = (detailUpdates != null)
                ? detailUpdates.stream().collect(Collectors.toMap(DetailUpdateDto::getProductId, d -> d))
                : java.util.Collections.emptyMap();

        boolean isInternal = "INTERNAL_TRANSFER".equals(ticket.getTransferType());

        if ("ACCEPTED".equals(newStatus) || "PARTIALLY_DAMAGED".equals(newStatus)) {
            String destBranchId = ticket.getDestination().getId();
            for (TransferDetail detail : ticket.getDetails()) {
                String productId = detail.getProduct().getId();

                // Leer cantidades del mapa si vinieron del front, o asumir 100% recibido sin daños
                DetailUpdateDto upd = updateMap.get(productId);
                BigInteger shipped = detail.getQuantityShipped();
                BigInteger received = (upd != null && upd.getQuantityReceived() != null)
                        ? BigInteger.valueOf(upd.getQuantityReceived())
                        : shipped;
                BigInteger damaged = (upd != null && upd.getQuantityDamaged() != null)
                        ? BigInteger.valueOf(upd.getQuantityDamaged())
                        : BigInteger.ZERO;

                // Validar coherencia: recibidos + dañados <= enviados
                if (received.add(damaged).compareTo(shipped) > 0) {
                    throw new IllegalArgumentException(
                            "Recibidos + dañados superan la cantidad enviada para el producto: " + productId);
                }

                detail.setQuantityReceived(received);
                detail.setQuantityDamaged(damaged);

                // Sumar SÓLO los recibidos OK al Almacén Destino
                Warehouse destWarehouse = warehouseRepository.findByBranchOfficeIdAndProductId(destBranchId, productId)
                        .orElse(null);

                if (destWarehouse != null) {
                    destWarehouse.setStock(destWarehouse.getStock().add(received));
                    warehouseRepository.save(destWarehouse);
                } else {
                    Warehouse newWarehouse = Warehouse.builder()
                            .product(detail.getProduct())
                            .branchOffice(ticket.getDestination())
                            .stock(received)
                            .unitaryCost(BigDecimal.ZERO)
                            .state("ACTIVE")
                            .maxProduct(BigInteger.valueOf(9999))
                            .minProduct(BigInteger.valueOf(1))
                            .build();
                    warehouseRepository.save(newWarehouse);
                }
            }
        }
        else if ("REJECTED".equals(newStatus) && isInternal) {
            // Devolver 100% al origen
            String sourceBranchId = ticket.getSourceId();
            for (TransferDetail detail : ticket.getDetails()) {
                BigInteger qty = detail.getQuantityShipped();
                Warehouse sourceWarehouse = warehouseRepository.findByBranchOfficeIdAndProductId(sourceBranchId, detail.getProduct().getId())
                        .orElseThrow(() -> new NoSuchElementException("No se encuentra el origen para devolver stock."));
                sourceWarehouse.setStock(sourceWarehouse.getStock().add(qty));
                warehouseRepository.save(sourceWarehouse);
            }
        }

        return transferTicketRepository.save(ticket);
    }
}
