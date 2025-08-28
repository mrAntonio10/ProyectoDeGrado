package com.upb.cores.utils;

import ch.qos.logback.core.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberUtilMod {

    public static void  throwNumberIsNullOrEmpty(BigInteger value, String attributeName) {
        if(value == null) {
             throw new NullPointerException("Valor [" +attributeName+ "]: No acepta vacío o nulo");
        }

        if(value.compareTo(BigInteger.valueOf(0)) == -1) {
            throw new IllegalArgumentException("Valor [" +attributeName+ "]: No acepta negativos");
        }
    }

    public static void  throwNumberIsNullOrEmpty(BigDecimal value, String attributeName) {
        if(value == null) {
            throw new NullPointerException("Valor [" +attributeName+ "]: No acepta vacío o nulo");
        }

        if(value.compareTo(BigDecimal.valueOf(0)) == -1) {
            throw new IllegalArgumentException("Valor [" +attributeName+ "]: No acepta negativos");
        }
    }

    public static void  throwNumberMaxDecimal(BigDecimal value, int maxScale,String attributeName) {
        if(value == null) {
            throw new NullPointerException("Valor [" +attributeName+ "]: No acepta vacío o nulo");
        }
        if(value.scale() > maxScale) {
            throw new IllegalArgumentException("Valor [" +attributeName+ "]: No acepta más de " + maxScale + " decimales.");
        }

        if(value.compareTo(BigDecimal.valueOf(0)) == -1) {
            throw new IllegalArgumentException("Valor [" +attributeName+ "]: No acepta negativos");
        }
    }

}
