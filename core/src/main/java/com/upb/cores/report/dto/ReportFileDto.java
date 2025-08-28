package com.upb.cores.report.dto;

import lombok.Data;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Data
public class ReportFileDto {
    private String filename;
//    private ByteArrayInputStream stream;
    private String base64;
    private int length;
}
