package com.upb.toffi.config.util;

import com.upb.models.utils.MessageGenericResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse<T> {
    private boolean success;
    private String message;
    private int statusCode;
    private T data;

    public static <T> GenericResponse<T> success(int statusCode, T data) {
        return GenericResponse.<T>builder()
                .message("SUCCESS!")
                .statusCode(statusCode)
                .data(data)
                .success(true)
                .build();
    }

    public static <T> GenericResponse<T> success(int statusCode, String message) {
        return GenericResponse.<T>builder()
                .message("SUCCESS!")
                .statusCode(statusCode)
                .data((T) MessageGenericResponseDto.builder().response(message).build())
                .success(true)
                .build();
    }

    public static <T> GenericResponse<T> error(int statusCode, String message) {
        return GenericResponse.<T>builder()
                .statusCode(statusCode)
                .message("ERROR!")
                .data((T) MessageGenericResponseDto.builder().response(message).build())
                .success(false)
                .build();
    }
}