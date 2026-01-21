package com.hse.Curriculum.Repository;
import com.hse.Curriculum.Models.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationRepository extends JpaRepository<Education, Integer> {

    /**
     * Buscar todos los registros de educación de un usuario con país cargado.
     * Ordena por fecha de último nivel (más reciente primero),
     * colocando los nulos al final.
     *
     * @param userId ID del usuario
     * @return Lista de educaciones del usuario
     */
    @Query("SELECT e FROM Education e LEFT JOIN FETCH e.country " +
            "WHERE e.user.userId = :userId " +
            "ORDER BY e.lastLevelDate DESC NULLS LAST")
    List<Education> findByUserIdWithCountry(@Param("userId") Integer userId);

    /**
     * Buscar un registro específico de educación verificando que pertenezca al usuario.
     * Útil para operaciones donde solo el dueño puede acceder.
     *
     * @param educationId ID del registro educativo
     * @param userId ID del usuario propietario
     * @return Optional con la educación si existe y pertenece al usuario
     */
    @Query("SELECT e FROM Education e LEFT JOIN FETCH e.country " +
            "WHERE e.educationId = :educationId AND e.user.userId = :userId")
    Optional<Education> findByIdAndUserId(
            @Param("educationId") Integer educationId,
            @Param("userId") Integer userId
    );

    /**
     * Buscar un registro de educación por ID con todos los detalles cargados.
     * Usado por administradores que necesitan ver información completa.
     * Incluye JOIN FETCH de country y user para evitar consultas adicionales.
     *
     * @param educationId ID del registro educativo
     * @return Optional con la educación y sus relaciones cargadas
     */
    @Query("SELECT e FROM Education e " +
            "LEFT JOIN FETCH e.country " +
            "LEFT JOIN FETCH e.user " +
            "WHERE e.educationId = :educationId")
    Optional<Education> findByIdWithDetails(@Param("educationId") Integer educationId);

    /**
     * Verificar si existe un registro de educación para un usuario específico.
     * Método derivado de Spring Data JPA (no necesita @Query).
     *
     * @param educationId ID del registro educativo
     * @param userId ID del usuario
     * @return true si el registro existe y pertenece al usuario
     */
    boolean existsByEducationIdAndUser_UserId(Integer educationId, Integer userId);

    /**
     * Contar cuántos registros educativos tiene un usuario.
     *
     * @param userId ID del usuario
     * @return Cantidad de registros
     */
    @Query("SELECT COUNT(e) FROM Education e WHERE e.user.userId = :userId")
    Long countByUserId(@Param("userId") Integer userId);

    /**
     * Buscar educaciones por tipo (primaria, media, superior).
     *
     * @param userId ID del usuario
     * @param typeEducation Tipo de educación
     * @return Lista de educaciones del tipo especificado
     */
    @Query("SELECT e FROM Education e " +
            "LEFT JOIN FETCH e.country " +
            "WHERE e.user.userId = :userId " +
            "AND e.typeEducation = :typeEducation " +
            "ORDER BY e.lastLevelDate DESC NULLS LAST")
    List<Education> findByUserIdAndTypeEducation(
            @Param("userId") Integer userId,
            @Param("typeEducation") com.hse.Curriculum.Enum.EducationTypeEnum typeEducation
    );

    /**
     * Eliminar todos los registros educativos de un usuario.
     * Usado cuando se elimina un usuario completo.
     *
     * @param userId ID del usuario
     */
    @Query("DELETE FROM Education e WHERE e.user.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}