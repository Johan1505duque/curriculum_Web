package com.hse.Curriculum.Controller;
import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Dto.UserDTO.Admin.*;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Models.Profiles;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Service.AuditService;
import com.hse.Curriculum.Service.ProfilesService;
import com.hse.Curriculum.Service.UsersService;
import com.hse.Curriculum.Exception.Users.UnauthorizedRoleException;
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
import java.util.stream.Collectors;

/**
 * Controlador para gestión de usuarios por parte del Administrador
 *
 * REQUIERE ROL: ADMIN
 *
 * Funcionalidades:
 * - Crear usuarios con rol y cargo asignado
 * - Actualizar cualquier campo de un usuario (incluyendo rol y cargo)
 * - Listar todos los usuarios
 * - Buscar usuarios por diferentes criterios
 * - Habilitar/Deshabilitar usuarios
 * - Ver detalles completos de un usuario
 *
 * Todas las acciones se registran en audit_logs
 */

@RestController
@RequestMapping("admin/users")
@Tag(name = "Admin - User Management", description = "Gestión completa de usuarios (solo Admin)")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminUsersController {

    private final UsersService usersService;
    private final ProfilesService profilesService;
    private final AuditService auditService;

    /**
     * Validar que el usuario autenticado sea Admin
     */
    private Users validateAdminUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();

        Users authenticatedUser = usersService.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (!authenticatedUser.isAdmin()) {
            throw new UnauthorizedRoleException(
                    "Esta acción requiere rol ADMIN pero el usuario tiene rol " +
                            authenticatedUser.getRole().getName()
            );
        }

        return authenticatedUser;
    }

    // ==================== CREATE ====================

    /**
     * POST - Crear usuario con rol y cargo (solo Admin)
     */
    @PostMapping
    @Operation(
            summary = "Crear usuario (Admin)",
            description = "Crea un usuario nuevo con rol y cargo asignados. Se crea automáticamente un perfil vacío."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado - requiere rol Admin"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    public ResponseEntity<ApiResponseDTO<UserDetailResponseDTO>> createUser(
            @Valid @RequestBody UserCreateByAdminDTO createDTO,
            HttpServletRequest request) {
        try {
            // 1. Validar que sea Admin
            Users adminUser = validateAdminUser();

            // 2. Crear usuario
            Users newUser = usersService.createUserByAdmin(createDTO);

            // 3. Crear perfil automáticamente
            Profiles profile = profilesService.createEmptyProfileForUser(newUser.getUserId());

            // 4. Registrar creación de usuario en auditoría
            auditService.logAction(
                    adminUser.getUserId(),
                    adminUser.getEmail(),
                    adminUser.getFirstName() + " " + adminUser.getLastName(),
                    "users",
                    newUser.getUserId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    newUser,
                    String.format("Admin creó usuario con rol %s y cargo %s",
                            newUser.getRole().getName(),
                            newUser.getCharge() != null ? newUser.getCharge().getNameCharge() : "sin cargo"),
                    request
            );

            // 5. Registrar creación de perfil en auditoría
            auditService.logAction(
                    adminUser.getUserId(),
                    adminUser.getEmail(),
                    adminUser.getFirstName() + " " + adminUser.getLastName(),
                    "profiles",
                    profile.getProfilesId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    profile,
                    "Perfil creado automáticamente por Admin",
                    request
            );

            // 6. Preparar respuesta
            UserDetailResponseDTO response = new UserDetailResponseDTO(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "Usuario creado exitosamente",
                            HttpStatus.CREATED.value(),
                            response
                    )
            );

        } catch (UnauthorizedRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("email")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ApiResponseDTO.error(
                                e.getMessage(),
                                HttpStatus.CONFLICT.value()
                        )
                );
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }
    }

    // ==================== READ ====================

    /**
     * GET - Obtener detalles completos de un usuario
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID (Admin)",
            description = "Obtiene toda la información de un usuario incluyendo rol y cargo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<UserDetailResponseDTO>> getUserById(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer id) {
        try {
            // Validar Admin
            validateAdminUser();

            // Buscar usuario
            Users user = usersService.getById(id);
            UserDetailResponseDTO response = new UserDetailResponseDTO(user);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Usuario encontrado",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (UnauthorizedRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );
        }
    }

    /**
     * GET - Listar todos los usuarios
     */
    @GetMapping
    @Operation(
            summary = "Listar todos los usuarios (Admin)",
            description = "Obtiene la lista completa de usuarios del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ApiResponseDTO<List<UserListResponseDTO>>> getAllUsers() {
        try {
            // Validar Admin
            validateAdminUser();

            // Obtener usuarios
            List<UserListResponseDTO> users = usersService.getAllUsers().stream()
                    .map(UserListResponseDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Usuarios obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            users
                    )
            );

        } catch (UnauthorizedRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        }
    }

    /**
     * GET - Listar usuarios activos
     */
    @GetMapping("/active")
    @Operation(
            summary = "Listar usuarios activos (Admin)",
            description = "Obtiene solo los usuarios con status=true"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ApiResponseDTO<List<UserListResponseDTO>>> getActiveUsers() {
        try {
            // Validar Admin
            validateAdminUser();

            // Obtener usuarios activos
            List<UserListResponseDTO> users = usersService.getActiveUsers().stream()
                    .map(UserListResponseDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Usuarios activos obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            users
                    )
            );

        } catch (UnauthorizedRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        }
    }

    /**
     * GET - Buscar usuarios por rol
     */
    @GetMapping("/role/{roleId}")
    @Operation(
            summary = "Listar usuarios por rol (Admin)",
            description = "Obtiene todos los usuarios que tienen un rol específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ApiResponseDTO<List<UserListResponseDTO>>> getUsersByRole(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Integer roleId) {
        try {
            // Validar Admin
            validateAdminUser();

            // Obtener usuarios por rol
            List<UserListResponseDTO> users = usersService.getUsersByRole(roleId).stream()
                    .map(UserListResponseDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Usuarios obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            users
                    )
            );

        } catch (UnauthorizedRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        }
    }



    // ==================== UPDATE ====================

    /**
     * PATCH - Actualizar usuario (incluyendo rol y cargo)
     */
    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario (Admin)",
            description = "Actualiza cualquier campo del usuario incluyendo rol y cargo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "Email ya existe")
    })
    public ResponseEntity<ApiResponseDTO<UserDetailResponseDTO>> updateUser(
            @Parameter(description = "ID del usuario", example = "5")
            @PathVariable Integer id,
            @Valid @RequestBody UserUpdateByAdminDTO updateDTO,
            HttpServletRequest request) {
        try {
            // 1. Validar Admin
            Users adminUser = validateAdminUser();

            // 2. Obtener estado anterior para auditoría
            Users oldUser = usersService.getById(id);

            // 3. Actualizar usuario
            Users updatedUser = usersService.updateUserByAdmin(id, updateDTO);

            // 4. Registrar en auditoría
            auditService.logAction(
                    adminUser.getUserId(),
                    adminUser.getEmail(),
                    adminUser.getFirstName() + " " + adminUser.getLastName(),
                    "users",
                    id,
                    AuditLog.AuditAction.UPDATE,
                    oldUser,
                    updatedUser,
                    "Admin actualizó usuario",
                    request
            );

            // 5. Preparar respuesta
            UserDetailResponseDTO response = new UserDetailResponseDTO(updatedUser);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Usuario actualizado exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (UnauthorizedRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponseDTO.error(
                                e.getMessage(),
                                HttpStatus.NOT_FOUND.value()
                        )
                );
            }
            if (e.getMessage().contains("email")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ApiResponseDTO.error(
                                e.getMessage(),
                                HttpStatus.CONFLICT.value()
                        )
                );
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }
    }

    /**
     * PATCH - Deshabilitar usuario
     */
    @PatchMapping("/{id}/disable")
    @Operation(
            summary = "Deshabilitar usuario (Admin)",
            description = "Desactiva un usuario del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario deshabilitado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> disableUser(
            @Parameter(description = "ID del usuario", example = "5")
            @PathVariable Integer id,
            HttpServletRequest request) {
        try {
            // 1. Validar Admin
            Users adminUser = validateAdminUser();

            // 2. Deshabilitar
            usersService.disableUser(id);

            // 3. Registrar en auditoría
            auditService.logAction(
                    adminUser.getUserId(),
                    adminUser.getEmail(),
                    adminUser.getFirstName() + " " + adminUser.getLastName(),
                    "users",
                    id,
                    AuditLog.AuditAction.DISABLE,
                    java.util.Map.of("status", true),
                    java.util.Map.of("status", false),
                    "Admin deshabilitó usuario",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Usuario deshabilitado exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (UnauthorizedRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
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
     * PATCH - Habilitar usuario
     */
    @PatchMapping("/{id}/enable")
    @Operation(
            summary = "Habilitar usuario (Admin)",
            description = "Reactiva un usuario deshabilitado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario habilitado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> enableUser(
            @Parameter(description = "ID del usuario", example = "5")
            @PathVariable Integer id,
            HttpServletRequest request) {
        try {
            // 1. Validar Admin
            Users adminUser = validateAdminUser();

            // 2. Habilitar
            usersService.enableUser(id);

            // 3. Registrar en auditoría
            auditService.logAction(
                    adminUser.getUserId(),
                    adminUser.getEmail(),
                    adminUser.getFirstName() + " " + adminUser.getLastName(),
                    "users",
                    id,
                    AuditLog.AuditAction.ENABLE,
                    java.util.Map.of("status", false),
                    java.util.Map.of("status", true),
                    "Admin habilitó usuario",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Usuario habilitado exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (UnauthorizedRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
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
}
