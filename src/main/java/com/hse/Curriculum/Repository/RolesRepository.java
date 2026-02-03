package com.hse.Curriculum.Repository;

import com.hse.Curriculum.Models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;
import java.util.List;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {
    /**
     * Busca un rol por su nombre exacto
     *
     * @param name Nombre del rol (ej: "ADMIN")
     * @return Optional con el rol si existe
     */
    Optional<Roles> findByName(String name);

    /**
     * Busca un rol por nombre ignorando mayúsculas/minúsculas
     *
     * @param name Nombre del rol
     * @return Optional con el rol si existe
     */
    @Query("SELECT r FROM Roles r WHERE UPPER(r.name) = UPPER(:name)")
    Optional<Roles> findByNameIgnoreCase(@Param("name") String name);

    /**
     * Busca todos los roles activos
     *
     * @param status Estado del rol (true = activo)
     * @return Lista de roles activos
     */
    @Query("SELECT r FROM Roles r WHERE r.status = :status ORDER BY r.name")
    List<Roles> findByStatus(@Param("status") Boolean status);

    /**
     * Busca todos los roles ordenados por nombre
     *
     * @return Lista de roles ordenados alfabéticamente
     */
    @Query("SELECT r FROM Roles r ORDER BY r.name")
    List<Roles> findAllOrderByName();

    /**
     * Verifica si existe un rol con un nombre específico
     *
     * @param name Nombre del rol
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);

    /**
     * Verifica si existe un rol con un nombre específico excluyendo un ID
     * Útil para validar nombres únicos al actualizar
     *
     * @param name Nombre del rol
     * @param roleId ID del rol a excluir
     * @return true si existe otro rol con ese nombre, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Roles r WHERE UPPER(r.name) = UPPER(:name) AND r.roleId != :roleId")
    boolean existsByNameAndRoleIdNot(@Param("name") String name, @Param("roleId") Integer roleId);

    /**
     * Cuenta el número de usuarios asignados a un rol específico
     *
     * @param roleId ID del rol
     * @return Número de usuarios con este rol
     */
    @Query("SELECT COUNT(u) FROM Users u WHERE u.role.roleId = :roleId")
    Long countUsersByRoleId(@Param("roleId") Integer roleId);

    /**
     * Busca roles por nombre parcial (para búsquedas)
     *
     * @param name Nombre parcial del rol
     * @return Lista de roles que coinciden
     */
    @Query("SELECT r FROM Roles r WHERE UPPER(r.name) LIKE UPPER(CONCAT('%', :name, '%')) ORDER BY r.name")
    List<Roles> searchByName(@Param("name") String name);

    /**
     * Obtiene roles con conteo de usuarios asignados
     * Devuelve una proyección con roleId, name, description, status y userCount
     *
     * @return Lista de objetos con información de roles y conteo de usuarios
     */
    @Query("SELECT r.roleId as roleId, r.name as name, r.description as description, " +
            "r.status as status, r.createdAt as createdAt, COUNT(u) as userCount " +
            "FROM Roles r LEFT JOIN Users u ON u.role.roleId = r.roleId " +
            "GROUP BY r.roleId, r.name, r.description, r.status, r.createdAt " +
            "ORDER BY r.name")
    List<RoleProjection> findAllWithUserCount();

    /**
     * Proyección para obtener roles con conteo de usuarios
     */
    interface RoleProjection {
        Integer getRoleId();
        String getName();
        String getDescription();
        Boolean getStatus();
        java.time.LocalDateTime getCreatedAt();
        Long getUserCount();
    }
}
