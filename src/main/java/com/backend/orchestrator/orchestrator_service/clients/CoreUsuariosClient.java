package com.backend.orchestrator.orchestrator_service.clients;

import com.backend.orchestrator.orchestrator_service.dto.response.UserByPhoneResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "core-usuarios", url = "${core.usuarios.url}")
public interface CoreUsuariosClient {

    @GetMapping("/core1/users/by-phone/{phone}")
    UserByPhoneResponseDTO findByPhone(
            @PathVariable("phone") String phone,
            @RequestHeader("x-session-id") String sessionId,
            @RequestHeader("x-trace-id") String traceId
    );
}
