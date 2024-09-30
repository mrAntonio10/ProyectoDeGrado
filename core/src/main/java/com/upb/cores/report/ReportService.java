package com.upb.cores.report;

import com.upb.cores.report.dto.ReportFileDto;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ReportService {

    ByteArrayOutputStream exportReport(String fileName, String report, String typeReport, Map<String, Object> params, List list) throws IOException, JRException;

    ReportFileDto outOfStockReport(Authentication auth, String productName, String idBranchOffice, String category, String maxOrMinLimit, Pageable pageable,
            Map<String, Object> params) throws JRException, IOException;
}
