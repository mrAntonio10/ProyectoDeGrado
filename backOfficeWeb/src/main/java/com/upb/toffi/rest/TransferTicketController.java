package com.upb.toffi.rest;

import com.upb.cores.TransferTicketService;
import com.upb.models.transferTicket.TransferTicket;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.transferTicket.CreateTransferTicketRequest;
import com.upb.toffi.rest.request.transferTicket.UpdateTicketStatusRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/transfer-tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class TransferTicketController {

    private final TransferTicketService transferTicketService;

    /**
     * GET /api/v1/transfer-tickets?branchId=xxx
     * Obtener todos los tickets donde el branchId es receptor (DESTINO).
     */
    @GetMapping("")
    public ResponseEntity<GenericResponse<List<TransferTicket>>> getTickets(
            @RequestParam(value = "branchId", defaultValue = "") String branchId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            List<TransferTicket> tickets = transferTicketService.getTickets(branchId, auth);
            return ok(GenericResponse.success(HttpStatus.OK.value(), tickets));
        } catch (Exception e) {
            log.error("Error al obtener tickets", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * GET /api/v1/transfer-tickets/{id}
     * Obtener un ticket por ID con su histórico de eventos.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<TransferTicket>> getTicketById(@PathVariable("id") String id) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(), transferTicketService.getTicketById(id)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener ticket {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * POST /api/v1/transfer-tickets
     * Crear un nuevo ticket de transferencia o abastecimiento.
     */
    @PostMapping("")
    public ResponseEntity<GenericResponse<TransferTicket>> createTicket(@RequestBody CreateTransferTicketRequest req) {
        try {
            List<String> productIds = req.getDetails().stream()
                    .map(CreateTransferTicketRequest.DetailItem::getProductId)
                    .collect(Collectors.toList());

            List<Integer> quantities = req.getDetails().stream()
                    .map(CreateTransferTicketRequest.DetailItem::getQuantityShipped)
                    .collect(Collectors.toList());

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            TransferTicket ticket = transferTicketService.createTicket(
                    req.getTransferType(), req.getSourceId(), req.getDestinationId(),
                    req.getComments(), productIds, quantities, auth);

            return ok(GenericResponse.success(HttpStatus.OK.value(), ticket));
        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.error("Error de datos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear ticket", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * PUT /api/v1/transfer-tickets/{id}/status
     * Registra un evento inmutable de cambio de estado (ACEPTAR / RECHAZAR / DAÑO).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<GenericResponse<TransferTicket>> updateStatus(
            @PathVariable("id") String id,
            @RequestBody UpdateTicketStatusRequest req) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            TransferTicket ticket = transferTicketService.updateTicketStatus(
                    id, req.getNewStatus(), req.getComments(),
                    req.getDetails() != null ? req.getDetails() : java.util.Collections.emptyList(),
                    auth);
            return ok(GenericResponse.success(HttpStatus.OK.value(), ticket));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar estado del ticket {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * POST /api/v1/transfer-tickets/{id}/events
     * Alias del endpoint /status para compatibilidad con el frontend actual.
     */
    @PostMapping("/{id}/events")
    public ResponseEntity<GenericResponse<TransferTicket>> addEvent(
            @PathVariable("id") String id,
            @RequestBody UpdateTicketStatusRequest req) {
        return updateStatus(id, req);
    }
}
