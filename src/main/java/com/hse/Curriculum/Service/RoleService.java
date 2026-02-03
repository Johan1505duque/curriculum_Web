package com.hse.Curriculum.Service;
import com.hse.Curriculum.Dto.RoleDTO.RoleCreateDTO;
import com.hse.Curriculum.Dto.RoleDTO.RoleResponseDTO;
import com.hse.Curriculum.Dto.RoleDTO.RoleUpdateDTO;
import com.hse.Curriculum.Exception.Role.RoleDuplicateException;
import com.hse.Curriculum.Exception.Role.RoleInUseException;
import com.hse.Curriculum.Exception.Role.RoleNotFoundException;
import com.hse.Curriculum.Exception.Role.RoleOperationNotAllowedException;
import com.hse.Curriculum.Models.Roles;
import com.hse.Curriculum.Repository.RolesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que maneja la lógica de negocio para los roles del sistema
 * Implementa operaciones CRUD y validaciones necesarias
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RolesRepository rolesRepository;

    // Roles del sistema que no se pueden eliminar
    private static final List<String> SYSTEM_ROLES = Arrays.asList("ADMIN", "USER");

    /**
     * Crea un nuevo rol en el sistema
     *
     * @param dto Datos del rol a crear
     * @return DTO con los datos del rol creado
     * @throws RoleDuplicateException Si ya existe un rol con ese nombre
     */
    @Transactional
    public RoleResponseDTO createRole(RoleCreateDTO dto) {
        log.info("Creando nuevo rol: {}", dto.getName());

        // Validar que el nombre no esté duplicado
        String roleName = dto.getName().toUpperCase();
        if (rolesRepository.existsByName(roleName)) {
            throw new RoleDuplicateException(roleName);
        }

        // Crear entidad Roles
        Roles role = Roles.builder()
                .name(roleName)
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : true)
                .build();

        // Guardar en base de datos
        Roles savedRole = rolesRepository.save(role);
        log.info("Rol creado exitosamente con ID: {}", savedRole.getRoleId());

        // Convertir a DTO de respuesta
        return convertToResponseDTO(savedRole, 0L);
    }

    /**
     * Obtiene todos los roles del sistema
     *
     * @return Lista de todos los roles con conteo de usuarios
     */
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> getAllRoles() {
        log.info("Obteniendo todos los roles");

        List<RolesRepository.RoleProjection> projections = rolesRepository.findAllWithUserCount();

        return projections.stream()
                .map(proj -> RoleResponseDTO.builder()
                        .roleId(proj.getRoleId())
                        .name(proj.getName())
                        .description(proj.getDescription())
                        .status(proj.getStatus())
                        .createdAt(proj.getCreatedAt())
                        .userCount(proj.getUserCount())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo los roles activos
     *
     * @return Lista de roles activos
     */
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> getActiveRoles() {
        log.info("Obteniendo roles activos");

        List<Roles> roles = rolesRepository.findByStatus(true);

        return roles.stream()
                .map(role -> {
                    Long userCount = rolesRepository.countUsersByRoleId(role.getRoleId());
                    return convertToResponseDTO(role, userCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un rol por su ID
     *
     * @param roleId ID del rol
     * @return DTO con los datos del rol
     * @throws RoleNotFoundException Si no se encuentra el rol
     */
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleById(Integer roleId) {
        log.info("Obteniendo rol con ID: {}", roleId);

        Roles role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        Long userCount = rolesRepository.countUsersByRoleId(roleId);
        return convertToResponseDTO(role, userCount);
    }

    /**
     * Obtiene un rol por su nombre
     *
     * @param name Nombre del rol
     * @return DTO con los datos del rol
     * @throws RoleNotFoundException Si no se encuentra el rol
     */
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleByName(String name) {
        log.info("Obteniendo rol con nombre: {}", name);

        Roles role = rolesRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RoleNotFoundException(name, true));

        Long userCount = rolesRepository.countUsersByRoleId(role.getRoleId());
        return convertToResponseDTO(role, userCount);
    }

    /**
     * Obtiene la entidad Roles por su ID (para uso interno)
     *
     * @param roleId ID del rol
     * @return Entidad Roles
     * @throws RoleNotFoundException Si no se encuentra el rol
     */
    @Transactional(readOnly = true)
    public Roles getRoleEntityById(Integer roleId) {
        return rolesRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));
    }

    /**
     * Actualiza un rol existente
     * Solo actualiza los campos que no son nulos en el DTO
     *
     * @param roleId ID del rol a actualizar
     * @param dto Datos a actualizar
     * @return DTO con los datos actualizados
     * @throws RoleNotFoundException Si no se encuentra el rol
     * @throws RoleDuplicateException Si el nuevo nombre ya existe
     * @throws RoleOperationNotAllowedException Si es un rol del sistema
     */
    @Transactional
    public RoleResponseDTO updateRole(Integer roleId, RoleUpdateDTO dto) {
        log.info("Actualizando rol con ID: {}", roleId);

        // Buscar rol existente
        Roles role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        // Verificar si es un rol del sistema
        if (SYSTEM_ROLES.contains(role.getName().toUpperCase())) {
            throw new RoleOperationNotAllowedException("No se puede modificar el rol del sistema: " + role.getName());
        }

        // Actualizar nombre si se proporciona
        if (dto.getName() != null && !dto.getName().isBlank()) {
            String newName = dto.getName().toUpperCase();

            // Validar que el nuevo nombre no esté duplicado
            if (!newName.equals(role.getName()) && rolesRepository.existsByNameAndRoleIdNot(newName, roleId)) {
                throw new RoleDuplicateException(newName);
            }

            role.setName(newName);
        }

        // Actualizar descripción si se proporciona
        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription());
        }

        // Actualizar estado si se proporciona
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }

        // Guardar cambios
        Roles updatedRole = rolesRepository.save(role);
        log.info("Rol ID: {} actualizado exitosamente", roleId);

        Long userCount = rolesRepository.countUsersByRoleId(roleId);
        return convertToResponseDTO(updatedRole, userCount);
    }

    /**
     * Elimina un rol del sistema
     * Solo se puede eliminar si no está en uso y no es un rol del sistema
     *
     * @param roleId ID del rol a eliminar
     * @throws RoleNotFoundException Si no se encuentra el rol
     * @throws RoleInUseException Si el rol está siendo usado
     * @throws RoleOperationNotAllowedException Si es un rol del sistema
     */
    @Transactional
    public void deleteRole(Integer roleId) {
        log.info("Eliminando rol con ID: {}", roleId);

        // Buscar rol
        Roles role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        // Verificar si es un rol del sistema
        if (SYSTEM_ROLES.contains(role.getName().toUpperCase())) {
            throw new RoleOperationNotAllowedException("No se puede eliminar el rol del sistema: " + role.getName());
        }

        // Verificar si está en uso
        Long userCount = rolesRepository.countUsersByRoleId(roleId);
        if (userCount > 0) {
            throw new RoleInUseException(role.getName(), userCount);
        }

        // Eliminar rol
        rolesRepository.delete(role);
        log.info("Rol ID: {} eliminado exitosamente", roleId);
    }

    /**
     * Cambia el estado de un rol (activar/desactivar)
     *
     * @param roleId ID del rol
     * @param status Nuevo estado
     * @return DTO con los datos actualizados
     * @throws RoleNotFoundException Si no se encuentra el rol
     * @throws RoleOperationNotAllowedException Si es un rol del sistema y se intenta desactivar
     */
    @Transactional
    public RoleResponseDTO changeRoleStatus(Integer roleId, Boolean status) {
        log.info("Cambiando estado del rol ID: {} a {}", roleId, status);

        Roles role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        // Verificar si es un rol del sistema que se intenta desactivar
        if (SYSTEM_ROLES.contains(role.getName().toUpperCase()) && !status) {
            throw new RoleOperationNotAllowedException("No se puede desactivar el rol del sistema: " + role.getName());
        }

        role.setStatus(status);
        Roles updatedRole = rolesRepository.save(role);

        log.info("Estado del rol ID: {} cambiado exitosamente", roleId);

        Long userCount = rolesRepository.countUsersByRoleId(roleId);
        return convertToResponseDTO(updatedRole, userCount);
    }

    /**
     * Busca roles por nombre parcial
     *
     * @param name Nombre parcial a buscar
     * @return Lista de roles que coinciden
     */
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> searchRolesByName(String name) {
        log.info("Buscando roles con nombre: {}", name);

        List<Roles> roles = rolesRepository.searchByName(name);

        return roles.stream()
                .map(role -> {
                    Long userCount = rolesRepository.countUsersByRoleId(role.getRoleId());
                    return convertToResponseDTO(role, userCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Roles a RoleResponseDTO
     *
     * @param role Entidad a convertir
     * @param userCount Cantidad de usuarios con este rol
     * @return DTO de respuesta
     */
    private RoleResponseDTO convertToResponseDTO(Roles role, Long userCount) {
        return RoleResponseDTO.builder()
                .roleId(role.getRoleId())
                .name(role.getName())
                .description(role.getDescription())
                .status(role.getStatus())
                .createdAt(role.getCreatedAt())
                .userCount(userCount != null ? userCount : 0L)
                .build();
    }
}
