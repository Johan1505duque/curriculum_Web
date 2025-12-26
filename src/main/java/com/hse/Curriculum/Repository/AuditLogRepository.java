package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    // Buscar logs por usuario
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // Buscar logs por tabla
    List<AuditLog> findByTableNameOrderByCreatedAtDesc(String tableName);

    // Buscar logs por tabla y registro específico
    List<AuditLog> findByTableNameAndRecordIdOrderByCreatedAtDesc(
            String tableName, Integer recordId);

    // Buscar logs por acción
    List<AuditLog> findByActionOrderByCreatedAtDesc(AuditLog.AuditAction action);

    // Buscar logs en un rango de fechas
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime start, LocalDateTime end);

    // Búsqueda avanzada con filtros múltiples
    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:userId IS NULL OR a.userId = :userId) AND " +
            "(:tableName IS NULL OR a.tableName = :tableName) AND " +
            "(:action IS NULL OR a.action = :action) AND " +
            "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR a.createdAt <= :endDate)")
    Page<AuditLog> findWithFilters(
            @Param("userId") Integer userId,
            @Param("tableName") String tableName,
            @Param("action") AuditLog.AuditAction action,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // Contar logs por usuario
    Long countByUserId(Integer userId);

    // Obtener últimas N acciones de un usuario
    List<AuditLog> findTop10ByUserIdOrderByCreatedAtDesc(Integer userId);
}
