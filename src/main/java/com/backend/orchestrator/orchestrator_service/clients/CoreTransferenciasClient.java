package com.backend.orchestrator.orchestrator_service.clients;

import com.backend.orchestrator.orchestrator_service.dto.request.CoreTransferRequestDTO;
import com.backend.orchestrator.orchestrator_service.dto.response.TransferResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "core-transferencias", url = "${core.transferencias.url}")
public interface CoreTransferenciasClient {

    @PostMapping("/core3/transfers")
    TransferResponseDTO ejecutarTransferencia(
            @RequestBody CoreTransferRequestDTO request,
            @RequestHeader("x-session-id") String sessionId,
            @RequestHeader("x-trace-id") String traceId
    );
}
