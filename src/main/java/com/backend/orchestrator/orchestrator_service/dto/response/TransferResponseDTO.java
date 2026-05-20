package com.backend.orchestrator.orchestrator_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferResponseDTO {
    private String transferenciaId;
    private BigDecimal monto;
    private String cuentaOrigen;
    private String cuentaDestino;
    private String nombreDestino;
    private String fechaTransaccion;
    private String estado;
}
