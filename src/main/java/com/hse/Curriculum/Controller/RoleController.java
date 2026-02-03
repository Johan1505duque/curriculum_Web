package com.hse.Curriculum.Controller;
import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Dto.RoleDTO.RoleCreateDTO;
import com.hse.Curriculum.Dto.RoleDTO.RoleResponseDTO;
import com.hse.Curriculum.Dto.RoleDTO.RoleUpdateDTO;
import com.hse.Curriculum.Exception.Role.*;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Models.Roles;
import com.hse.Curriculum.Service.AuditService;
import com.hse.Curriculum.Service.RoleService;
import com.hse.Curriculum.Service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de roles del sistema
 * Proporciona endpoints para operaciones CRUD sobre roles
 * Solo accesible por usuarios con rol ADMIN
 */
@RestController
@RequestMapping("/roles")
@Tag(name = "Role Management", description = "Gestión de roles del sistema (Solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final UsersService usersService;
    private final AuditService auditService;

    /**
     * POST - Crear un nuevo rol
     * Solo ADMIN puede crear roles
     */
    @PostMapping
    @Operation(
            summary = "Crear rol",
            description = "Crea un nuevo rol en el sistema. Solo accesible por ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "409", description = "Ya existe un rol con ese nombre")
    })
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> createRole(
            @Valid @RequestBody RoleCreateDTO dto,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado y verificar que sea ADMIN
            var authenticatedUser = getAuthenticatedUser();
            if (!isAdmin(authenticatedUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado - Solo administradores",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Crear rol
            RoleResponseDTO response = roleService.createRole(dto);

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "roles",
                    response.getRoleId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    response,
                    "Creación de rol: " + dto.getName(),
                    request
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "Rol creado exitosamente",
                            HttpStatus.CREATED.value(),
                            response
                    )
            );

        } catch (RoleDuplicateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.CONFLICT.value()
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }
    }

    /**
     * GET - Obtener todos los roles
     * Usuarios normales pueden ver roles, pero solo ADMIN puede gestionarlos
     */
    @GetMapping
    @Operation(
            summary = "Obtener todos los roles",
            description = "Obtiene la lista completa de roles con conteo de usuarios asignados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<List<RoleResponseDTO>>> getAllRoles() {
        try {
            List<RoleResponseDTO> roles = roleService.getAllRoles();

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Roles obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            roles
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDTO.error(
                            "Error al obtener roles",
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    )
            );
        }
    }

    /**
     * GET - Obtener solo roles activos
     */
    @GetMapping("/active")
    @Operation(
            summary = "Obtener roles activos",
            description = "Obtiene solo los roles que están activos en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles activos obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<List<RoleResponseDTO>>> getActiveRoles() {
        try {
            List<RoleResponseDTO> roles = roleService.getActiveRoles();

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Roles activos obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            roles
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDTO.error(
                            "Error al obtener roles activos",
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    )
            );
        }
    }

    /**
     * GET - Obtener un rol específico por ID
     */
    @GetMapping("/{roleId}")
    @Operation(
            summary = "Obtener rol por ID",
            description = "Obtiene los detalles de un rol específico incluyendo conteo de usuarios"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> getRoleById(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Integer roleId) {
        try {
            RoleResponseDTO response = roleService.getRoleById(roleId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Rol obtenido exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (RoleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );
        }
    }

    /**
     * GET - Obtener un rol por su nombre
     */
    @GetMapping("/name/{name}")
    @Operation(
            summary = "Obtener rol por nombre",
            description = "Obtiene un rol específico por su nombre (ej: ADMIN, USER)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> getRoleByName(
            @Parameter(description = "Nombre del rol", example = "ADMIN")
            @PathVariable String name) {
        try {
            RoleResponseDTO response = roleService.getRoleByName(name);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Rol obtenido exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (RoleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );
        }
    }

    /**
     * GET - Buscar roles por nombre parcial
     */
    @GetMapping("/search")
    @Operation(
            summary = "Buscar roles",
            description = "Busca roles por nombre parcial (ej: buscar 'admin' encontrará 'ADMIN', 'ADMINISTRATOR')"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda completada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<List<RoleResponseDTO>>> searchRoles(
            @Parameter(description = "Nombre parcial a buscar", example = "admin")
            @RequestParam String name) {
        try {
            List<RoleResponseDTO> roles = roleService.searchRolesByName(name);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Búsqueda completada exitosamente",
                            HttpStatus.OK.value(),
                            roles
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDTO.error(
                            "Error al buscar roles",
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    )
            );
        }
    }

    /**
     * PATCH - Actualizar un rol
     * Solo ADMIN puede actualizar roles
     * No se pueden actualizar roles del sistema (ADMIN, USER)
     */
    @PatchMapping("/{roleId}")
    @Operation(
            summary = "Actualizar rol",
            description = "Actualiza parcialmente un rol. Solo ADMIN. No se pueden modificar roles del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN o rol del sistema"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
            @ApiResponse(responseCode = "409", description = "Nombre de rol ya existe")
    })
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> updateRole(
            @Parameter(description = "ID del rol", example = "3")
            @PathVariable Integer roleId,
            @Valid @RequestBody RoleUpdateDTO dto,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado y verificar que sea ADMIN
            var authenticatedUser = getAuthenticatedUser();
            if (!isAdmin(authenticatedUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado - Solo administradores",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Obtener datos anteriores para auditoría
            Roles oldRole = roleService.getRoleEntityById(roleId);

            // Actualizar rol
            RoleResponseDTO response = roleService.updateRole(roleId, dto);

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "roles",
                    roleId,
                    AuditLog.AuditAction.UPDATE,
                    oldRole,
                    response,
                    "Actualización de rol: " + response.getName(),
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Rol actualizado exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (RoleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );

        } catch (RoleDuplicateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.CONFLICT.value()
                    )
            );

        } catch (RoleOperationNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        }
    }

    /**
     * PATCH - Cambiar estado de un rol (activar/desactivar)
     * Solo ADMIN puede cambiar estados
     * No se pueden desactivar roles del sistema
     */
    @PatchMapping("/{roleId}/status")
    @Operation(
            summary = "Cambiar estado de rol",
            description = "Activa o desactiva un rol. Solo ADMIN. No se pueden desactivar roles del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN o rol del sistema"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> changeRoleStatus(
            @Parameter(description = "ID del rol", example = "3")
            @PathVariable Integer roleId,
            @Parameter(description = "Nuevo estado (true = activo, false = inactivo)", example = "true")
            @RequestParam Boolean status,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado y verificar que sea ADMIN
            var authenticatedUser = getAuthenticatedUser();
            if (!isAdmin(authenticatedUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado - Solo administradores",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Obtener datos anteriores para auditoría
            Roles oldRole = roleService.getRoleEntityById(roleId);

            // Cambiar estado
            RoleResponseDTO response = roleService.changeRoleStatus(roleId, status);

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "roles",
                    roleId,
                    AuditLog.AuditAction.UPDATE,
                    oldRole,
                    response,
                    "Cambio de estado de rol: " + response.getName() + " a " + (status ? "activo" : "inactivo"),
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Estado del rol cambiado exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (RoleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );

        } catch (RoleOperationNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        }
    }

    /**
     * DELETE - Eliminar un rol
     * Solo ADMIN puede eliminar roles
     * No se pueden eliminar roles del sistema ni roles en uso
     */
    @DeleteMapping("/{roleId}")
    @Operation(
            summary = "Eliminar rol",
            description = "Elimina un rol. Solo ADMIN. No se pueden eliminar roles del sistema ni roles asignados a usuarios"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN o rol del sistema"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
            @ApiResponse(responseCode = "409", description = "Rol está en uso")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteRole(
            @Parameter(description = "ID del rol", example = "3")
            @PathVariable Integer roleId,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado y verificar que sea ADMIN
            var authenticatedUser = getAuthenticatedUser();
            if (!isAdmin(authenticatedUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado - Solo administradores",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Obtener datos antes de eliminar (para auditoría)
            Roles role = roleService.getRoleEntityById(roleId);

            // Eliminar rol
            roleService.deleteRole(roleId);

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "roles",
                    roleId,
                    AuditLog.AuditAction.DELETE,
                    role,
                    null,
                    "Eliminación de rol: " + role.getName(),
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Rol eliminado exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (RoleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );

        } catch (RoleInUseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.CONFLICT.value()
                    )
            );

        } catch (RoleOperationNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        }
    }

    /**
     * Helper para obtener el usuario autenticado desde el contexto de seguridad
     *
     * @return Usuario autenticado
     * @throws RuntimeException Si no hay usuario autenticado
     */
    private com.hse.Curriculum.Models.Users getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String email = authentication.getName();
        return usersService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    /**
     * Helper para verificar si el usuario tiene rol de ADMIN
     *
     * @param user Usuario a verificar
     * @return true si es ADMIN, false en caso contrario
     */
    private boolean isAdmin(com.hse.Curriculum.Models.Users user) {
        if (user.getRole() == null) {
            return false;
        }
        return "ADMIN".equals(user.getRole().getName());
    }
}
