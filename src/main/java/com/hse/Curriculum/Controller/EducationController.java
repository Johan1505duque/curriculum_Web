package com.hse.Curriculum.Controller;
import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Dto.EducationDTO.EducationCreateDTO;
import com.hse.Curriculum.Dto.EducationDTO.EducationResponseDTO;
import com.hse.Curriculum.Dto.EducationDTO.EducationUpdateDTO;
import com.hse.Curriculum.Exception.Education.*;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Models.Education;
import com.hse.Curriculum.Service.AuditService;
import com.hse.Curriculum.Service.EducationService;
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

@RestController
@RequestMapping("education")
@Tag(name = "Education Management", description = "Gestión de registros educativos de usuarios")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor

public class EducationController {
    private final EducationService educationService;
    private final UsersService usersService;
    private final AuditService auditService;

    /**
     * POST - Crear un nuevo registro de educación
     */
    @PostMapping
    @Operation(
            summary = "Crear registro de educación",
            description = "Crea un nuevo registro educativo para el usuario autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario o país no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<EducationResponseDTO>> createEducation(
            @Valid @RequestBody EducationCreateDTO dto,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            // Crear registro
            EducationResponseDTO response = educationService.createEducation(userId, dto);

            // Registrar en auditoría
            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "education",
                    response.getEducationId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    response,
                    "Creación de registro educativo",
                    request
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "Registro de educación creado exitosamente",
                            HttpStatus.CREATED.value(),
                            response
                    )
            );

        } catch (InvalidEducationDateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()
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
     * GET - Obtener todos los registros de educación del usuario autenticado
     */
    @GetMapping
    @Operation(
            summary = "Obtener registros educativos",
            description = "Obtiene todos los registros educativos del usuario autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<List<EducationResponseDTO>>> getMyEducations() {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            List<EducationResponseDTO> educations = educationService.getEducationsByUserId(userId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registros educativos obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            educations
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
     * GET - Obtener un registro específico de educación
     */
    @GetMapping("/{educationId}")
    @Operation(
            summary = "Obtener registro educativo por ID",
            description = "Obtiene un registro educativo específico. Usuarios pueden ver solo los suyos, ADMIN puede ver todos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ApiResponseDTO<EducationResponseDTO>> getEducationById(
            @Parameter(description = "ID del registro educativo", example = "1")
            @PathVariable Integer educationId) {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();
            boolean isAdmin = authenticatedUser.isAdmin();

            EducationResponseDTO response = educationService.getEducationById(educationId, userId, isAdmin);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registro educativo obtenido exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (EducationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );
        }
    }

    /**
     * PATCH - Actualizar un registro de educación
     */
    @PatchMapping("/{educationId}")
    @Operation(
            summary = "Actualizar registro educativo",
            description = "Actualiza parcialmente un registro educativo. Solo el propietario puede editar"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ApiResponseDTO<EducationResponseDTO>> updateEducation(
            @Parameter(description = "ID del registro educativo", example = "1")
            @PathVariable Integer educationId,
            @Valid @RequestBody EducationUpdateDTO dto,
            HttpServletRequest request) {
        try {
            // Verificar autorización (solo el dueño puede editar)
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            // Obtener datos anteriores para auditoría
            Education oldEducation = educationService.getEducationEntityById(educationId);

            // Verificar que el registro pertenece al usuario
            if (!oldEducation.getUser().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado para actualizar este registro",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Actualizar
            EducationResponseDTO response = educationService.updateEducation(educationId, userId, dto);

            // Registrar en auditoría
            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "education",
                    educationId,
                    AuditLog.AuditAction.UPDATE,
                    oldEducation,
                    response,
                    "Actualización de registro educativo",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registro educativo actualizado exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (EducationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );

        } catch (InvalidEducationDateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()
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
     * DELETE - Eliminar un registro de educación
     */
    @DeleteMapping("/{educationId}")
    @Operation(
            summary = "Eliminar registro educativo",
            description = "Elimina un registro educativo. El propietario y ADMIN pueden eliminar"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteEducation(
            @Parameter(description = "ID del registro educativo", example = "1")
            @PathVariable Integer educationId,
            HttpServletRequest request) {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();
            // ✅ VALIDACIÓN SEGURA: Verificar que el usuario tenga rol
            if (authenticatedUser.getRole() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "Usuario sin rol asignado. Contacte al administrador.",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            boolean isAdmin = authenticatedUser.getRole().getName().equals("ADMIN");

            // Obtener datos antes de eliminar (para auditoría)
            Education education = educationService.getEducationEntityById(educationId);

            // Verificar autorización: propietario o admin
            if (!isAdmin && !education.getUser().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado para eliminar este registro",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Eliminar
            educationService.deleteEducation(educationId, userId, isAdmin);

            // Registrar en auditoría
            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "education",
                    educationId,
                    AuditLog.AuditAction.DELETE,
                    education,
                    null,
                    "Eliminación de registro educativo",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registro educativo eliminado exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (EducationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );
        }
    }

    /**
     * GET - Obtener todos los registros de educación de un usuario (solo ADMIN)
     */
    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Obtener registros educativos de un usuario (ADMIN)",
            description = "Obtiene todos los registros educativos de cualquier usuario. Solo accesible por ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<List<EducationResponseDTO>>> getUserEducations(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId) {
        try {
            var authenticatedUser = getAuthenticatedUser();
            boolean isAdmin = authenticatedUser.isAdmin();

            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado - Solo administradores",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            List<EducationResponseDTO> educations = educationService.getEducationsByUserId(userId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registros educativos obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            educations
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
     * Helper para obtener el usuario autenticado
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
