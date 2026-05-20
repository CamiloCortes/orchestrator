package com.backend.orchestrator.orchestrator_service.services;

import com.backend.orchestrator.orchestrator_service.clients.CoreTransferenciasClient;
import com.backend.orchestrator.orchestrator_service.clients.CoreUsuariosClient;
import com.backend.orchestrator.orchestrator_service.dto.request.CoreTransferRequestDTO;
import com.backend.orchestrator.orchestrator_service.dto.response.TransferResponseDTO;
import com.backend.orchestrator.orchestrator_service.dto.response.UserByPhoneResponseDTO;
import com.backend.orchestrator.orchestrator_service.exceptions.BusinessException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final CoreUsuariosClient coreUsuariosClient;
    private final CoreTransferenciasClient coreTransferenciasClient;

    public TransferResponseDTO transferir(
            String userIdOrigen,
            String telefonoOrigen,
            String telefonoDestino,
            BigDecimal monto,
            String descripcion,
            String sessionId,
            String traceId) {

        log.info("Iniciando transferencia: usuarioOrigen={} telefonoOrigen={} telefonoDestino={}",
                userIdOrigen, telefonoOrigen, telefonoDestino);

        if (telefonoOrigen != null && telefonoOrigen.equals(telefonoDestino)) {
            log.warn("Intento de transferencia al mismo usuario: {}", telefonoDestino);
            throw new BusinessException(
                    "SAME_USER_TRANSFER",
                    "No puede transferirse a sí mismo",
                    400
            );
        }

        log.info("Validando usuario destino: {}", telefonoDestino);
        UserByPhoneResponseDTO destino = buscarUsuario(telefonoDestino, sessionId, traceId);

        if (!"ACTIVO".equalsIgnoreCase(destino.getEstado())) {
            log.warn("Usuario destino inactivo: {} estado={}", telefonoDestino, destino.getEstado());
            throw new BusinessException("USER_INACTIVE", "Usuario destino inactivo", 403);
        }

        CoreTransferRequestDTO body = CoreTransferRequestDTO.builder()
                .userIdOrigen(userIdOrigen)
                .telefonoDestino(telefonoDestino)
                .monto(monto)
                .descripcion(descripcion)
                .build();

        log.info("Ejecutando transferencia para userIdOrigen={} hacia telefonoDestino={}", userIdOrigen, telefonoDestino);
        TransferResponseDTO response = ejecutarTransferencia(body, sessionId, traceId);

        log.info("Transferencia exitosa: id={}", response.getTransferenciaId());
        return response;
    }

    @CircuitBreaker(name = "coreUsuarios", fallbackMethod = "fallbackBuscarUsuario")
    @Retry(name = "coreUsuarios")
    public UserByPhoneResponseDTO buscarUsuario(String phone, String sessionId, String traceId) {
        try {
            return coreUsuariosClient.findByPhone(phone, sessionId, traceId);
        } catch (FeignException.NotFound e) {
            throw new BusinessException("PHONE_NOT_FOUND", "Teléfono no encontrado", 404);
        } catch (FeignException e) {
            log.error("Error en Core Usuarios: {}", e.getMessage(), e);
            throw new BusinessException("CORE_USUARIOS_ERROR", "Error en servicio de usuarios", 503);
        }
    }

    public UserByPhoneResponseDTO fallbackBuscarUsuario(
            String phone, String sessionId, String traceId, Throwable ex) {
        if (ex instanceof BusinessException) {
            throw (BusinessException) ex;
        }
        log.error("Circuit Breaker - Core Usuarios no disponible: {}", ex.getMessage(), ex);
        throw new BusinessException(
                "CORE_USUARIOS_UNAVAILABLE",
                "Servicio de usuarios no disponible",
                503
        );
    }

    @CircuitBreaker(name = "coreTransferencias", fallbackMethod = "fallbackEjecutarTransferencia")
    @Retry(name = "coreTransferencias")
    public TransferResponseDTO ejecutarTransferencia(
            CoreTransferRequestDTO body, String sessionId, String traceId) {
        try {
            return coreTransferenciasClient.ejecutarTransferencia(body, sessionId, traceId);
        } catch (FeignException.BadRequest e) {
            if (isSaldoInsuficiente(e)) {
                throw new BusinessException("SALDO_INSUFICIENTE", "Saldo insuficiente", 400);
            }
            log.error("Error de transferencia en Core Transferencias: {}", e.getMessage(), e);
            throw new BusinessException("TRANSFER_FAILED", "Error en la transferencia", 400);
        } catch (FeignException e) {
            log.error("Error en Core Transferencias: {}", e.getMessage(), e);
            throw new BusinessException("CORE_TRANSFERENCIAS_ERROR", "Error en servicio de transferencias", 503);
        }
    }

    public TransferResponseDTO fallbackEjecutarTransferencia(
            CoreTransferRequestDTO body, String sessionId, String traceId, Throwable ex) {
        if (ex instanceof BusinessException) {
            throw (BusinessException) ex;
        }
        log.error("Circuit Breaker - Core Transferencias no disponible: {}", ex.getMessage(), ex);
        throw new BusinessException(
                "CORE_TRANSFERENCIAS_UNAVAILABLE",
                "Servicio de transferencias no disponible",
                503
        );
    }

    private boolean isSaldoInsuficiente(FeignException.BadRequest e) {
        String content = e.contentUTF8();
        return content != null && content.contains("SALDO_INSUFICIENTE");
    }
}
