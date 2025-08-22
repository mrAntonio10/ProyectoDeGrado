package com.upb.cores.report.impl;

import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.BranchOfficeService;
import com.upb.cores.report.ReportService;
import com.upb.cores.report.dto.ReportFileDto;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.document.dto.SalesUserDocumentDto;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.models.warehouse.dto.WarehousePagedDto;
import com.upb.repositories.DocumentRepository;
import com.upb.repositories.UserBranchOfficeRepository;
import com.upb.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserBranchOfficeRepository userBranchOfficeRepository;
    private final WarehouseRepository warehouseRepository;
    private final BranchOfficeService branchOfficeService;
    private final DocumentRepository documentRepository;

    @Override
    public ByteArrayOutputStream exportReport(String fileName, String report, String typeReport, Map<String, Object> params, List list) throws IOException, JRException {
        InputStream reportStream = getClass().getResourceAsStream("/reports/"+report);
        if (reportStream == null) {
            throw new FileNotFoundException("El archivo de reporte no se encontró");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(typeReport.equalsIgnoreCase("excel")) {
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(stream));
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setDetectCellType(true);
            configuration.setCollapseRowSpan(true);
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        } else {
            JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
        }

        return stream;
    }

    @Override
    public ReportFileDto outOfStockReport(Authentication auth, String productName, String idBranchOffice, String category, String maxOrMinLimit, Pageable pageable, Map<String, Object> params) throws JRException, IOException {
        productName = (!StringUtil.isNullOrEmpty(productName) ? "%" +productName.toUpperCase() +"%" : null);
        idBranchOffice = (!StringUtil.isNullOrEmpty(idBranchOffice) ? idBranchOffice : null);
        maxOrMinLimit = (!StringUtil.isNullOrEmpty(maxOrMinLimit) ? maxOrMinLimit.toUpperCase() : null );
        /*BEBIDA, ALMUERZO, SANDWICH, EMPANADA*/
        category = (!StringUtil.isNullOrEmpty(category) ? "%"+ category.toUpperCase() +"%" : null);

        String idRol = auth.getAuthorities().stream().toList().get(0).toString();
        User user = (User) auth.getPrincipal();

        List<User_BranchOffice> ub = userBranchOfficeRepository.getUser_BranchOfficeByIdUserAndIdRol(user.getId(), idRol);
        BranchOffice b = branchOfficeService.getBranchOfficeById(idBranchOffice);

        if(ub.isEmpty()) {
            throw new NoSuchElementException("No fue posible recuperar los valores correspondientes al usuario");
        }

        Page<WarehousePagedDto> pagedResp =  warehouseRepository.getWarehousePageable(ub.get(0).getBranchOffice().getEnterprise().getId(),
                idBranchOffice, productName, category, maxOrMinLimit,  Pageable.unpaged());

        Map<String, Object> mapParams = new HashMap<>();
            mapParams.put("username", user.getEmail());
            mapParams.put("branchOffice", b.getName());
            mapParams.put("generatedDate", this.getDateTime(java.time.LocalDateTime.now()));

        List<WarehousePagedDto> list = new ArrayList<>();


        String fileName = "reporte_de_quiebre.pdf";
        ReportFileDto rep = new ReportFileDto();
        rep.setFilename(fileName);
        ByteArrayOutputStream stream = exportReport(fileName, "REP01_PRODUCT_WAREHOUSE.jrxml", "pdf", mapParams, list);
        byte[] bs = stream.toByteArray();

        rep.setBase64(getStreamAsBase64(new ByteArrayInputStream(bs)));
        rep.setLength(bs.length);

        return rep;
    }

    @Override
    public ReportFileDto userSalesReport(Authentication auth, String filter, LocalDate date, Pageable pageable, Map<String, Object> params) throws JRException, IOException {
        filter = !StringUtil.isNullOrEmpty(filter) ? "%" +filter.toUpperCase()+ "%" : null;

        User user = (User) auth.getPrincipal();
        String idRol = auth.getAuthorities().stream().toList().get(0).toString();

        log.info("USUARIO ID{}", user.getId());
        List<User_BranchOffice> ub = userBranchOfficeRepository.getUser_BranchOfficeByIdUserAndIdRol(user.getId(), idRol);
        BranchOffice b = branchOfficeService.getBranchOfficeById(ub.get(0).getBranchOffice().getId());


        Long start = date.atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();
        Long finish = date.plusDays(1).atStartOfDay(ZoneId.of("America/La_Paz")).toInstant().toEpochMilli();



        Map<String, Object> mapParams = new HashMap<>();
            mapParams.put("branchOffice", b.getName());
            mapParams.put("userPOS", user.getName() +" "+ user.getLastname());
            mapParams.put("generatedDate", this.getDateTime(date.atStartOfDay()));

        Page<SalesUserDocumentDto> pagedResp = this.documentRepository.getSalesUserDocumetPageable(user.getId(), filter, start, finish, "ACEPTADO",Pageable.unpaged());

        List<SalesUserDocumentDto> list = new ArrayList<>();
        list = pagedResp.stream().toList();

        String fileName = "reporte_de_ventas.pdf";
        ReportFileDto rep = new ReportFileDto();
        rep.setFilename(fileName);
        ByteArrayOutputStream stream = exportReport(fileName, "REP02_USER_SALES_REPORT.jrxml", "pdf", mapParams, list);
        byte[] bs = stream.toByteArray();

        rep.setBase64(getStreamAsBase64(new ByteArrayInputStream(bs)));
        rep.setLength(bs.length);

        return rep;
    }

    public String getStreamAsBase64(ByteArrayInputStream stream) throws IOException {
        if (stream != null) {
            byte[] bytes = stream.readAllBytes(); // O usa stream.read(byte[]) en versiones más antiguas
            return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }

    private String getDateTime(LocalDateTime dt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd-MMMM-yyyy", new Locale("es", "ES"));

        return dt.atZone(ZoneId.of("America/La_Paz")).format(formatter).substring(0, 1).toUpperCase() + dt.atZone(ZoneId.of("America/La_Paz")).format(formatter).substring(1).toLowerCase();
    }
}
