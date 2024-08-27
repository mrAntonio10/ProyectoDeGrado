package com.upb.cores.utils;

import ch.qos.logback.core.util.StringUtil;

public class StringUtilMod {

    public static void  throwStringIsNullOrEmpty(String value, String attributeName) {
        if(StringUtil.isNullOrEmpty(value)) {
             throw new NullPointerException("Valor [" +attributeName+ "]: No puede ser vacío o nulo");
        }
    }
}
