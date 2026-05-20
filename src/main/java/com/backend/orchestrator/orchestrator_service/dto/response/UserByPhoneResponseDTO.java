package com.backend.orchestrator.orchestrator_service.dto.response;

import lombok.Data;

@Data
public class UserByPhoneResponseDTO {
    private String userId;
    private String username;
    private String numeroCelular;
    private String estado;
}
