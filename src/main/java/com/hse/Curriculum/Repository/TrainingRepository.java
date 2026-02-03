package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Training
 * Proporciona métodos para acceder y manipular datos de capacitaciones en la base de datos
 *
 * Métodos heredados de JpaRepository (disponibles automáticamente):
 * - save(Training entity): Guarda o actualiza una entidad
 * - findById(Integer id): Busca por ID
 * - findAll(): Obtiene todos los registros
 * - delete(Training entity): Elimina una entidad
 * - deleteById(Integer id): Elimina por ID
 * - count(): Cuenta total de registros
 * - existsById(Integer id): Verifica si existe por ID
 */
@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {

    /**
     * Busca todos los registros de capacitación de un usuario específico
     * Ordenados por fecha de finalización descendente
     *
     * @param userId ID del usuario
     * @return Lista de capacitaciones del usuario
     */
    @Query("SELECT t FROM Training t WHERE t.user.userId = :userId ORDER BY t.completionDate DESC, t.createdAt DESC")
    List<Training> findByUserIdOrderByCompletionDateDesc(@Param("userId") Integer userId);

    /**
     * Busca todos los registros de capacitación completados de un usuario
     *
     * @param userId ID del usuario
     * @param isCompleted Estado de finalización
     * @return Lista de capacitaciones filtradas por estado
     */
    @Query("SELECT t FROM Training t WHERE t.user.userId = :userId AND t.isCompleted = :isCompleted ORDER BY t.completionDate DESC")
    List<Training> findByUserIdAndIsCompleted(@Param("userId") Integer userId, @Param("isCompleted") Boolean isCompleted);

    /**
     * Busca un registro de capacitación por ID incluyendo información del usuario
     * Utiliza JOIN FETCH para evitar consultas N+1
     *
     * @param trainingId ID del registro
     * @return Optional con el registro si existe
     */
    @Query("SELECT t FROM Training t JOIN FETCH t.user WHERE t.trainingId = :trainingId")
    Optional<Training> findByIdWithUser(@Param("trainingId") Integer trainingId);

    /**
     * Busca capacitaciones por tipo de capacitación
     *
     * @param userId ID del usuario
     * @param trainingType Tipo de capacitación
     * @return Lista de capacitaciones del tipo especificado
     */
    @Query("SELECT t FROM Training t WHERE t.user.userId = :userId AND t.trainingType = :trainingType ORDER BY t.completionDate DESC")
    List<Training> findByUserIdAndTrainingType(@Param("userId") Integer userId, @Param("trainingType") String trainingType);

    /**
     * Busca capacitaciones por institución
     *
     * @param userId ID del usuario
     * @param institution Nombre de la institución
     * @return Lista de capacitaciones de la institución especificada
     */
    @Query("SELECT t FROM Training t WHERE t.user.userId = :userId AND t.institution = :institution ORDER BY t.completionDate DESC")
    List<Training> findByUserIdAndInstitution(@Param("userId") Integer userId, @Param("institution") String institution);

    /**
     * Cuenta el número total de capacitaciones de un usuario
     *
     * @param userId ID del usuario
     * @return Número total de capacitaciones
     */
    @Query("SELECT COUNT(t) FROM Training t WHERE t.user.userId = :userId")
    Long countByUserId(@Param("userId") Integer userId);

    /**
     * Cuenta el número de capacitaciones completadas de un usuario
     *
     * @param userId ID del usuario
     * @return Número de capacitaciones completadas
     */
    @Query("SELECT COUNT(t) FROM Training t WHERE t.user.userId = :userId AND t.isCompleted = true")
    Long countCompletedByUserId(@Param("userId") Integer userId);

    /**
     * Verifica si existe un registro de capacitación con un nombre específico para un usuario
     * Útil para evitar duplicados
     *
     * @param userId ID del usuario
     * @param courseName Nombre del curso
     * @return true si existe, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Training t WHERE t.user.userId = :userId AND LOWER(t.courseName) = LOWER(:courseName)")
    boolean existsByUserIdAndCourseName(@Param("userId") Integer userId, @Param("courseName") String courseName);
}