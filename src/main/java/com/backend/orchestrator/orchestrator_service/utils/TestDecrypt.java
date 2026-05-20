package com.backend.orchestrator.orchestrator_service.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class TestDecrypt {

    @Bean
    public CommandLineRunner testDecryptor(SessionDecryptor decryptor) {
        return args -> {
            String cifradoDePython = "5b831c498ca6bbfd0607355cef750b0e:b61e7a04c44289b289e174360a0c8345:f50e24a7693a912eea35604ccb7b6c6d9917b66e4f9509b0ea86c3e92af4f4c1f30f9353802bb3c57dc02b61b3581025e1aa1ef95cc381a1a26c97f5edfdba23fe";
            try {
                Map<String, Object> resultado = decryptor.decrypt(cifradoDePython);
                System.out.println("✅ Descifrado exitoso: " + resultado);
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        };
    }
}