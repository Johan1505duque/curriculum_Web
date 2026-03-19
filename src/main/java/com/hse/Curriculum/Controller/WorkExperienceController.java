package com.hse.Curriculum.Controller;

import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Dto.WorkExperienceDTO.*;
import com.hse.Curriculum.Exception.WorkExperience.*;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Models.WorkExperience;
import com.hse.Curriculum.Service.AuditService;
import com.hse.Curriculum.Service.UsersService;
import com.hse.Curriculum.Service.WorkExperienceService;
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

@RestController
@RequestMapping("work-experience")
@Tag(name = "Work Experience Management", description = "Gestión de registros de experiencia laboral")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class WorkExperienceController {
    private final WorkExperienceService workExperienceService;
    private final UsersService usersService;
    private final AuditService auditService;

    /**
     * POST - Crear un nuevo registro de experiencia laboral
     */
    @PostMapping
    @Operation(summary = "Crear registro de experiencia laboral",
            description = "Crea un nuevo registro de experiencia laboral para el usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o fechas incorrectas"),
            @ApiResponse(responseCode = "404", description = "Usuario, país, departamento o municipio no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<WorkExperienceResponseDTO>> createWorkExperience(
            @Valid @RequestBody WorkExperienceCreateDTO dto,
            HttpServletRequest request) {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            WorkExperienceResponseDTO response = workExperienceService.createWorkExperience(userId, dto);

            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "work_experience",
                    response.getWorkExperienceId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    response,
                    "Creación de registro de experiencia laboral",
                    request
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "Registro de experiencia laboral creado exitosamente",
                            HttpStatus.CREATED.value(),
                            response
                    )
            );

        } catch (InvalidWorkExperienceDateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    /**
     * GET - Obtener todos los registros del usuario autenticado
     */
    @GetMapping
    @Operation(summary = "Obtener mis registros de experiencia laboral",
            description = "Obtiene todos los registros de experiencia laboral del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<List<WorkExperienceResponseDTO>>> getMyWorkExperiences() {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            List<WorkExperienceResponseDTO> list = workExperienceService.getWorkExperiencesByUserId(userId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registros de experiencia laboral obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            list
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    /**
     * GET - Obtener un registro específico por ID
     */
    @GetMapping("/{workExperienceId}")
    @Operation(summary = "Obtener registro de experiencia laboral por ID",
            description = "El usuario puede ver solo sus registros; ADMIN puede ver todos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro obtenido exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<WorkExperienceResponseDTO>> getWorkExperienceById(
            @Parameter(description = "ID del registro de experiencia laboral", example = "1")
            @PathVariable Integer workExperienceId) {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();
            boolean isAdmin = authenticatedUser.isAdmin();

            WorkExperienceResponseDTO response =
                    workExperienceService.getWorkExperienceById(workExperienceId, userId, isAdmin);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registro de experiencia laboral obtenido exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );
        } catch (WorkExperienceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    /**
     * PATCH - Actualizar un registro de experiencia laboral
     */
    @PatchMapping("/{workExperienceId}")
    @Operation(summary = "Actualizar registro de experiencia laboral",
            description = "Actualiza parcialmente un registro. Solo el propietario puede editar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o fechas incorrectas"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<WorkExperienceResponseDTO>> updateWorkExperience(
            @Parameter(description = "ID del registro de experiencia laboral", example = "1")
            @PathVariable Integer workExperienceId,
            @Valid @RequestBody WorkExperienceUpdateDTO dto,
            HttpServletRequest request) {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            WorkExperience oldWorkExperience = workExperienceService.getWorkExperienceEntityById(workExperienceId);

            if (!oldWorkExperience.getUser().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado para actualizar este registro",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            WorkExperienceResponseDTO response =
                    workExperienceService.updateWorkExperience(workExperienceId, userId, dto);

            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "work_experience",
                    workExperienceId,
                    AuditLog.AuditAction.UPDATE,
                    oldWorkExperience,
                    response,
                    "Actualización de registro de experiencia laboral",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registro de experiencia laboral actualizado exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (WorkExperienceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        } catch (InvalidWorkExperienceDateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    /**
     * DELETE - Eliminar un registro de experiencia laboral
     */
    @DeleteMapping("/{workExperienceId}")
    @Operation(summary = "Eliminar registro de experiencia laboral",
            description = "El propietario y el ADMIN pueden eliminar registros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteWorkExperience(
            @Parameter(description = "ID del registro de experiencia laboral", example = "1")
            @PathVariable Integer workExperienceId,
            HttpServletRequest request) {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            if (authenticatedUser.getRole() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "Usuario sin rol asignado. Contacte al administrador.",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            boolean isAdmin = authenticatedUser.getRole().getName().equals("ADMIN");

            WorkExperience workExperience = workExperienceService.getWorkExperienceEntityById(workExperienceId);

            if (!isAdmin && !workExperience.getUser().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado para eliminar este registro",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            workExperienceService.deleteWorkExperience(workExperienceId, userId, isAdmin);

            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "work_experience",
                    workExperienceId,
                    AuditLog.AuditAction.DELETE,
                    workExperience,
                    null,
                    "Eliminación de registro de experiencia laboral",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registro de experiencia laboral eliminado exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (WorkExperienceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    /**
     * GET - Obtener registros de un usuario específico (solo ADMIN)
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener registros de experiencia laboral de un usuario (ADMIN)",
            description = "Obtiene todos los registros de experiencia laboral de cualquier usuario. Solo accesible por ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<List<WorkExperienceResponseDTO>>> getUserWorkExperiences(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId) {
        try {
            var authenticatedUser = getAuthenticatedUser();

            if (!authenticatedUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado - Solo administradores",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            List<WorkExperienceResponseDTO> list = workExperienceService.getWorkExperiencesByUserId(userId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registros de experiencia laboral obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            list
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    /**
     * Helper - Obtener usuario autenticado desde el contexto de seguridad
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
}
