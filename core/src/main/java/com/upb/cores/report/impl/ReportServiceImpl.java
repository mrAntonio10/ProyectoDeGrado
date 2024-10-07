package com.upb.cores.report.impl;

import ch.qos.logback.core.util.StringUtil;
import com.upb.cores.BranchOfficeService;
import com.upb.cores.report.ReportService;
import com.upb.cores.report.dto.ReportFileDto;
import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.user.User;
import com.upb.models.user_branchOffice.User_BranchOffice;
import com.upb.models.warehouse.dto.WarehousePagedDto;
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
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserBranchOfficeRepository userBranchOfficeRepository;
    private final WarehouseRepository warehouseRepository;
    private final BranchOfficeService branchOfficeService;

    @Override
    public ByteArrayOutputStream exportReport(String fileName, String report, String typeReport, Map<String, Object> params, List list) throws IOException, JRException {

        File file = ResourceUtils.getFile("classpath:reports/"+report);
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
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

        Page<WarehousePagedDto> pagedResp =  warehouseRepository.getEnterprisePageable(ub.get(0).getBranchOffice().getEnterprise().getId(),
                idBranchOffice, productName, category, maxOrMinLimit, pageable);

        Map<String, Object> mapParams = new HashMap<>();
            mapParams.put("username", user.getEmail());
            mapParams.put("branchOffice", b.getName());

        List<WarehousePagedDto> list = new ArrayList<>();
        if(!pagedResp.isEmpty()) {
            list = pagedResp.stream().toList();
        }

        String fileName = "reporte_de_quiebre.pdf";
        ReportFileDto rep = new ReportFileDto();
        rep.setFilename(fileName);
        ByteArrayOutputStream stream = exportReport(fileName, "REP01_PRODUCT_WAREHOUSE.jrxml", "pdf", mapParams, list);
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
}
