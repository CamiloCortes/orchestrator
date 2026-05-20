package com.backend.orchestrator.orchestrator_service.clients;

import com.backend.orchestrator.orchestrator_service.dto.request.BalanceRequestDTO;
import com.backend.orchestrator.orchestrator_service.dto.response.BalanceResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "core-saldo", url = "${core.saldo.url}")
public interface CoreSaldoClient {

    @PostMapping("/core2/balance")
    BalanceResponseDTO consultarSaldo(
            @RequestBody BalanceRequestDTO request,
            @RequestHeader("x-session-id") String sessionId,
            @RequestHeader("x-trace-id") String traceId
    );
}
