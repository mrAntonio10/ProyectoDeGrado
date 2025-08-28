package com.upb.toffi.rest.report;


import com.upb.cores.DocumentService;
import com.upb.cores.report.ReportService;
import com.upb.cores.report.dto.ReportFileDto;
import com.upb.models.document.dto.SalesUserDocumentDto;
import com.upb.toffi.config.util.GenericResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ReportController {

    private final ReportService reportService;

    @GetMapping("min-products-warehouse")
    public ResponseEntity<GenericResponse<ReportFileDto>> reporteDeQuiebre(@RequestParam(value = "filter", defaultValue = "") String filterByProductName,
                                                   @RequestParam(value = "idBranchOffice", defaultValue = "") String idBranchOffice,
                                                   @RequestParam(value = "category", defaultValue = "") String category,
                                                   @RequestParam(value = "limit", defaultValue = "") String maxOrMinLimit,
                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "10000") Integer pageSize,
                                                   @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                   @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
    ) throws IOException, JRException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    (this.reportService.outOfStockReport(authentication, filterByProductName, idBranchOffice, category, maxOrMinLimit, pageable, null)))
            );
        } catch(NoSuchElementException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

    @GetMapping("user-sales-report")
    public ResponseEntity<GenericResponse<ReportFileDto>> getUserSalesReport(@RequestParam(value = "filter", defaultValue = "") String filter,
                                                                                                      @RequestParam(value = "date", defaultValue = "#{T(java.time.LocalDateTime).now()}")
                                                                                                      @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
                                                                                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                      @RequestParam(value = "size", defaultValue = "5") Integer pageSize,
                                                                                                      @RequestParam(value = "sortDir", defaultValue = "DESC")  String sortDir,
                                                                                                      @RequestParam(value = "sortBy", defaultValue = "paymentMethod") String sortBy
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            PageRequest pageable = PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            return ok(GenericResponse.success(HttpStatus.OK.value(),
                    (this.reportService.userSalesReport(authentication, filter, date, pageable, null)))
            );
        } catch(NoSuchElementException e) {
            log.error("Error {}, causa {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error genérico al obtener", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error en el servidor. Favor contactarse con el administrador."));
        }
    }

}
