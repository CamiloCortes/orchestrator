package com.backend.orchestrator.orchestrator_service.controllers;

import com.backend.orchestrator.orchestrator_service.dto.request.TransferRequestDTO;
import com.backend.orchestrator.orchestrator_service.dto.response.TransferResponseDTO;
import com.backend.orchestrator.orchestrator_service.exceptions.BusinessException;
import com.backend.orchestrator.orchestrator_service.services.TransferService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
@Slf4j
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponseDTO> transfer(
            @Valid @RequestBody TransferRequestDTO request,
            HttpServletRequest httpRequest) {

        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) httpRequest.getAttribute("user");

        if (user == null) {
            throw new BusinessException(
                    "SESSION_INVALID",
                    "Sesión no encontrada en el request",
                    401
            );
        }

        String userIdOrigen = String.valueOf(user.get("userId"));
        String telefonoOrigen = String.valueOf(user.get("numeroCelular"));
        String sessionId = (String) httpRequest.getAttribute("sessionId");
        String traceId = (String) httpRequest.getAttribute("traceId");

        log.info("POST /transfers - userIdOrigen={} traceId={}", userIdOrigen, traceId);

        TransferResponseDTO response = transferService.transferir(
                userIdOrigen,
                telefonoOrigen,
                request.getTelefonoDestino(),
                request.getMonto(),
                request.getDescripcion(),
                sessionId,
                traceId
        );

        return ResponseEntity.ok(response);
    }
}
