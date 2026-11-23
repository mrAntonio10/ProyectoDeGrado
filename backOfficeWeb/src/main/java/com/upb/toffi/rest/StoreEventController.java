package com.upb.toffi.rest;

import com.upb.cores.StoreEventService;
import com.upb.models.event.dto.EventProductDto;
import com.upb.models.event.dto.StoreEventDto;
import com.upb.toffi.config.util.GenericResponse;
import com.upb.toffi.rest.request.event.ApplyDiscountRequest;
import com.upb.toffi.rest.request.event.AssignEventProductsRequest;
import com.upb.toffi.rest.request.event.CreateStoreEventRequest;
import com.upb.toffi.rest.request.event.UpdateStoreEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class StoreEventController {

    private final StoreEventService storeEventService;

    /**
     * Público (Sin Login): Obtiene todos los eventos activos (Novedades, Descuentos, Navidad, etc.)
     * filtrando sus prendas por región (SANTA_CRUZ o LA_PAZ).
     */
    @GetMapping("/public")
    public ResponseEntity<GenericResponse<List<StoreEventDto>>> getPublicEvents(
            @RequestParam(value = "region", defaultValue = "") String region) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(), storeEventService.getAllEvents(region)));
        } catch (Exception e) {
            log.error("Error al obtener eventos públicos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor al obtener eventos."));
        }
    }

    /**
     * Público (Sin Login): Obtiene un evento por ID o código (ej. NOVEDADES, DESCUENTOS, NAVIDAD) y región.
     */
    @GetMapping("/public/{idOrCode}")
    public ResponseEntity<GenericResponse<StoreEventDto>> getPublicEventByIdOrCode(
            @PathVariable("idOrCode") String idOrCode,
            @RequestParam(value = "region", defaultValue = "") String region) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    storeEventService.getEventByIdOrCode(idOrCode, region)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener evento {}", idOrCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor al obtener el evento."));
        }
    }

    /**
     * Admin: Lista todos los eventos (incluyendo Novedades, Descuentos y eventos creados por el admin).
     */
    @GetMapping("")
    public ResponseEntity<GenericResponse<List<StoreEventDto>>> getAllEvents(
            @RequestParam(value = "region", defaultValue = "") String region) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(), storeEventService.getAllEvents(region)));
        } catch (Exception e) {
            log.error("Error al obtener eventos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @GetMapping("/{idOrCode}")
    public ResponseEntity<GenericResponse<StoreEventDto>> getEventById(
            @PathVariable("idOrCode") String idOrCode,
            @RequestParam(value = "region", defaultValue = "") String region) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    storeEventService.getEventByIdOrCode(idOrCode, region)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener evento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * Admin ABM: Crear nuevo evento (ej. NAVIDAD) seleccionando 1 o varias prendas.
     */
    @PostMapping("")
    public ResponseEntity<GenericResponse<StoreEventDto>> createEvent(@RequestBody CreateStoreEventRequest req) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    storeEventService.createEvent(
                            auth,
                            req.getName(),
                            req.getDescription(),
                            req.getStartDate(),
                            req.getEndDate(),
                            req.getProductIds(),
                            req.getDiscountType(),
                            req.getDiscountValue()
                    )));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear evento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * Admin ABM: Actualizar evento y su selección de prendas.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<StoreEventDto>> updateEvent(
            @PathVariable("id") String id,
            @RequestBody UpdateStoreEventRequest req) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    storeEventService.updateEvent(
                            auth,
                            id,
                            req.getName(),
                            req.getDescription(),
                            req.getStartDate(),
                            req.getEndDate(),
                            req.getProductIds(),
                            req.getDiscountType(),
                            req.getDiscountValue()
                    )));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar evento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * Admin ABM: Eliminar evento personalizado (Novedades y Descuentos están protegidos).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<StoreEventDto>> deleteEvent(@PathVariable("id") String id) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    storeEventService.deleteEvent(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al eliminar evento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * Admin: Asignar 1 o varios productos a un evento.
     */
    @PostMapping("/{id}/products")
    public ResponseEntity<GenericResponse<StoreEventDto>> assignProductsToEvent(
            @PathVariable("id") String eventId,
            @RequestBody AssignEventProductsRequest req) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    storeEventService.assignProductsToEvent(
                            auth,
                            eventId,
                            req.getProductIds(),
                            req.getDiscountType(),
                            req.getDiscountValue()
                    )));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al asignar prendas al evento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * Admin: Quitar una prenda de un evento.
     */
    @DeleteMapping("/{id}/products/{idProduct}")
    public ResponseEntity<GenericResponse<StoreEventDto>> removeProductFromEvent(
            @PathVariable("id") String eventId,
            @PathVariable("idProduct") String productId) {
        try {
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    storeEventService.removeProductFromEvent(eventId, productId)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al quitar prenda del evento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * Admin Descuentos: Aplica descuento (MONTO_FIJO o PORCENTUAL) a 1 o varias prendas,
     * registrando qué Administrador aceptó/aplicó el descuento.
     */
    @PostMapping("/discounts/apply")
    public ResponseEntity<GenericResponse<List<EventProductDto>>> applyDiscount(@RequestBody ApplyDiscountRequest req) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    storeEventService.applyAdminDiscount(
                            auth,
                            req.getProductIds(),
                            req.getDiscountType(),
                            req.getDiscountValue()
                    )));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(GenericResponse.error(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error al aplicar descuento admin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    /**
     * Admin Descuentos: Quita el descuento de una prenda.
     */
    @DeleteMapping("/discounts/{idProduct}")
    public ResponseEntity<GenericResponse<String>> removeDiscount(@PathVariable("idProduct") String idProduct) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            storeEventService.removeAdminDiscount(auth, idProduct);
            return ok(GenericResponse.success(HttpStatus.OK.value(), "Descuento removido exitosamente."));
        } catch (Exception e) {
            log.error("Error al quitar descuento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }
}
