package com.upb.cores.utils;

import ch.qos.logback.core.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtilMod {

    public static void  throwStringIsNullOrEmpty(String value, String attributeName) {
        if(StringUtil.isNullOrEmpty(value)) {
             throw new NullPointerException("Valor [" +attributeName+ "]: No acepta vacío o nulo");
        }
    }

    public static void notNullEmailMatcher(String value, String attributeName) {
        Pattern pattern = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);
        
        if(StringUtil.isNullOrEmpty(value)) {
            throw new NullPointerException("Valor [" +attributeName+ "]: No acepta vacío o nulo");
        }

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Valor [" + attributeName + "]: No es una dirección de correo electrónico válida");
        }
    }

    public static void notNullStringMaxLength(String value, int length, String attributeName) {
        if(StringUtil.isNullOrEmpty(value)) {
            throw new NullPointerException("Valor [" +attributeName+ "]: No acepta vacío o nulo");
        }

        if (value.length() > length) {
            throw new IllegalArgumentException("Valor [" + attributeName + "]: No acepta más de " +length+ " caracteres.");
        }
    }

    public static void notNullNumberMatcherMaxLength(String value, int length, String attributeName) {

        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(value);

        if(StringUtil.isNullOrEmpty(value)) {
            throw new NullPointerException("Valor [" +attributeName+ "]: No acepta vacío o nulo");
        }

        if (value.length() > length) {
            throw new IllegalArgumentException("Valor [" + attributeName + "]: No acepta más de " +length+ " caracteres.");
        }

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Valor [" + attributeName + "]: No es numérico");
        }
    }

    public static void canNullNumberMatcherMaxLength(String value, int length, String attributeName) {

        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(value);

        if (value.length() > length) {
            throw new IllegalArgumentException("Valor [" + attributeName + "]: No acepta más de " +length+ " caracteres.");
        }

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Valor [" + attributeName + "]: No es numérico");
        }
    }

}
