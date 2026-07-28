package com.upb.toffi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTestRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTestRunner.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseTestRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            // Ejecutamos un SELECT sencillo (en Oracle se suele usar SELECT 1 FROM DUAL)
            Integer result = jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);
            if (result != null && result == 1) {
                logger.info("========================================");
                logger.info("          CONECTADO CON EXITO           ");
                logger.info("========================================");
            }
        } catch (Exception e) {
            logger.error("========================================");
            logger.error("           ERROR DE CONEXION            ");
            logger.error("========================================");
            logger.error("Detalle del error: {}", e.getMessage());
        }
    }
}
