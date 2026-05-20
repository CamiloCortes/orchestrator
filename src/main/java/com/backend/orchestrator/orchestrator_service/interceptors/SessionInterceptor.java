package com.backend.orchestrator.orchestrator_service.interceptors;

import com.backend.orchestrator.orchestrator_service.utils.SessionDecryptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SessionInterceptor.class);
    private static final String SESSION_ID_HEADER = "x-session-id";
    private static final String SESSION_PREFIX = "session:";
    private static final String USER_ATTRIBUTE = "user";
    private static final String SESSION_ID_ATTRIBUTE = "sessionId";

    private final StringRedisTemplate redisTemplate;
    private final SessionDecryptor sessionDecryptor;
    private final ObjectMapper objectMapper;

    public SessionInterceptor(StringRedisTemplate redisTemplate,
                              SessionDecryptor sessionDecryptor,
                              ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.sessionDecryptor = sessionDecryptor;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        String sessionId = request.getHeader(SESSION_ID_HEADER);
        if (sessionId == null || sessionId.isBlank()) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "SESSION_MISSING", "SessionId requerido");
            return false;
        }

        String encrypted;
        try {
            encrypted = redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
        } catch (Exception e) {
            log.error("Error consultando Redis", e);
            writeError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "REDIS_ERROR", "Error del servicio");
            return false;
        }

        if (encrypted == null) {
            log.warn("Sesión expirada o inválida: {}", sessionId);
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "SESSION_EXPIRED", "Sesión expirada o inválida");
            return false;
        }

        Map<String, Object> sessionData;
        try {
            sessionData = sessionDecryptor.decrypt(encrypted);
        } catch (Exception e) {
            log.error("Error descifrando sesión", e);
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "SESSION_INVALID", "Sesión corrupta");
            return false;
        }

        request.setAttribute(USER_ATTRIBUTE, sessionData);
        request.setAttribute(SESSION_ID_ATTRIBUTE, sessionId);
        return true;
    }

    private void writeError(HttpServletResponse response, int status,
                            String errorCode, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> body = Map.of("errorCode", errorCode, "message", message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
