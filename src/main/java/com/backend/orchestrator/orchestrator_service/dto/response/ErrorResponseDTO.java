package com.backend.orchestrator.orchestrator_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    private String errorCode;
    private String message;
}
