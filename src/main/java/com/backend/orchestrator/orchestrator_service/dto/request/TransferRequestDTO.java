package com.backend.orchestrator.orchestrator_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {

    @NotBlank
    @Pattern(regexp = "^\\d{10}$")
    private String telefonoDestino;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal monto;

    @Size(max = 255)
    private String descripcion;
}
