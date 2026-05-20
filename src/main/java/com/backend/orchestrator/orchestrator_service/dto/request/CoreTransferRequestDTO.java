package com.backend.orchestrator.orchestrator_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class CoreTransferRequestDTO {
    private String userIdOrigen;
    private String telefonoDestino;
    private BigDecimal monto;
    private String descripcion;
}
