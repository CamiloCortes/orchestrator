package com.backend.orchestrator.orchestrator_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceResponseDTO {
    private String numeroCuenta;
    private BigDecimal saldo;
    private String estado;
}
