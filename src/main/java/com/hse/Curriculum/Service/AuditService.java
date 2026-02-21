package com.hse.Curriculum.Service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Registra una acción en el log de auditoría de forma asíncrona
     * Usa una transacción independiente para no afectar la operación principal
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(
            Integer userId,
            String userEmail,
            String userName,
            String tableName,
            Integer recordId,
            AuditLog.AuditAction action,
            Object oldValues,
            Object newValues,
            String description,
            HttpServletRequest request
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .userEmail(userEmail)
                    .userName(userName)
                    .tableName(tableName)
                    .recordId(recordId)
                    .action(action)
                    .oldValues(convertToJson(oldValues))
                    .newValues(convertToJson(newValues))
                    .description(description)
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Auditoría registrada: {} - {} en tabla {}", action, userName, tableName);

        } catch (Exception e) {
            log.error("Error al registrar auditoría: {}", e.getMessage());
            // No lanzamos la excepción para no afectar la operación principal
        }
    }

    /**
     * Método simplificado para operaciones sin valores antiguos/nuevos
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSimpleAction(
            Integer userId,
            String userEmail,
            String roleName,
            String chargeName,
            String userName,
            AuditLog.AuditAction action,
            String description,
            HttpServletRequest request
    ) {
        String fullDescription = description + " - Rol: " + roleName;


        logAction(userId, userEmail, userName, "system", null, action,
                null, null, description, request);
    }

    /**
     * Convierte un objeto a JSON
     */
    private String convertToJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir objeto a JSON: {}", e.getMessage());
            return object.toString();
        }
    }

    /**
     * Obtiene la IP real del cliente considerando proxies
     */
    private String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
