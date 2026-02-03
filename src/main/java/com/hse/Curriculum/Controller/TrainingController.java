package com.hse.Curriculum.Controller;
import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Dto.TrainingDTO.TrainingCreateDTO;
import com.hse.Curriculum.Dto.TrainingDTO.TrainingResponseDTO;
import com.hse.Curriculum.Dto.TrainingDTO.TrainingUpdateDTO;
import com.hse.Curriculum.Exception.Training.*;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Models.Training;
import com.hse.Curriculum.Service.AuditService;
import com.hse.Curriculum.Service.TrainingService;
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
 * Controlador REST para la gestión de capacitaciones y cursos
 * Proporciona endpoints para operaciones CRUD sobre registros de capacitación
 * Todos los endpoints requieren autenticación JWT
 */
@RestController
@RequestMapping("/trainings")
@Tag(name = "Training Management", description = "Gestión de capacitaciones y cursos de usuarios")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class TrainingController{
    private final TrainingService trainingService;
    private final UsersService usersService;
    private final AuditService auditService;

    /**
     * POST - Crear un nuevo registro de capacitación
     * El usuario autenticado será el propietario del registro
     */
    @PostMapping
    @Operation(
            summary = "Crear registro de capacitación",
            description = "Crea un nuevo registro de capacitación o curso para el usuario autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o inconsistentes"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<TrainingResponseDTO>> createTraining(
            @Valid @RequestBody TrainingCreateDTO dto,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado desde el token JWT
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            // Crear registro de capacitación
            TrainingResponseDTO response = trainingService.createTraining(userId, dto);

            // Registrar acción en auditoría
            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "trainings",
                    response.getTrainingId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    response,
                    "Creación de registro de capacitación: " + dto.getCourseName(),
                    request
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "Registro de capacitación creado exitosamente",
                            HttpStatus.CREATED.value(),
                            response
                    )
            );

        } catch (InvalidCompletionDateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()
                    )
            );

        } catch (InvalidTrainingDataException e) {
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
     * GET - Obtener todos los registros de capacitación del usuario autenticado
     */
    @GetMapping
    @Operation(
            summary = "Obtener mis capacitaciones",
            description = "Obtiene todos los registros de capacitación del usuario autenticado, ordenados por fecha de finalización"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<List<TrainingResponseDTO>>> getMyTrainings() {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            List<TrainingResponseDTO> trainings = trainingService.getTrainingsByUserId(userId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registros de capacitación obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            trainings
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
     * GET - Obtener capacitaciones completadas del usuario autenticado
     */
    @GetMapping("/completed")
    @Operation(
            summary = "Obtener capacitaciones completadas",
            description = "Obtiene solo los registros de capacitación completados del usuario autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<List<TrainingResponseDTO>>> getCompletedTrainings() {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            List<TrainingResponseDTO> trainings = trainingService.getCompletedTrainings(userId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Capacitaciones completadas obtenidas exitosamente",
                            HttpStatus.OK.value(),
                            trainings
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDTO.error(
                            "Error al obtener capacitaciones completadas",
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    )
            );
        }
    }

    /**
     * GET - Obtener capacitaciones en curso del usuario autenticado
     */
    @GetMapping("/in-progress")
    @Operation(
            summary = "Obtener capacitaciones en curso",
            description = "Obtiene solo los registros de capacitación que están en curso (no completados) del usuario autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<List<TrainingResponseDTO>>> getInProgressTrainings() {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            List<TrainingResponseDTO> trainings = trainingService.getInProgressTrainings(userId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Capacitaciones en curso obtenidas exitosamente",
                            HttpStatus.OK.value(),
                            trainings
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDTO.error(
                            "Error al obtener capacitaciones en curso",
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    )
            );
        }
    }

    /**
     * GET - Obtener un registro específico de capacitación por ID
     * Usuarios pueden ver solo sus propios registros
     * ADMIN y SUPPORT pueden ver todos los registros
     */
    @GetMapping("/{trainingId}")
    @Operation(
            summary = "Obtener capacitación por ID",
            description = "Obtiene un registro específico de capacitación. Usuarios ven solo los suyos, ADMIN y SUPPORT pueden ver todos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para ver este registro"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<TrainingResponseDTO>> getTrainingById(
            @Parameter(description = "ID del registro de capacitación", example = "1")
            @PathVariable Integer trainingId) {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();
            boolean isAdmin = isAdminOrSupport(authenticatedUser);

            TrainingResponseDTO response = trainingService.getTrainingById(trainingId, userId, isAdmin);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registro de capacitación obtenido exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (TrainingNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );

        } catch (TrainingUnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        }
    }

    /**
     * PATCH - Actualizar un registro de capacitación
     * El propietario, ADMIN y SUPPORT pueden actualizar registros
     * Actualización parcial: solo se modifican los campos enviados
     */
    @PatchMapping("/{trainingId}")
    @Operation(
            summary = "Actualizar capacitación",
            description = "Actualiza parcialmente un registro de capacitación. El propietario, ADMIN y SUPPORT pueden editar registros"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para actualizar este registro"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<TrainingResponseDTO>> updateTraining(
            @Parameter(description = "ID del registro de capacitación", example = "1")
            @PathVariable Integer trainingId,
            @Valid @RequestBody TrainingUpdateDTO dto,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            // Validación segura: Verificar que el usuario tenga rol asignado
            if (authenticatedUser.getRole() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "Usuario sin rol asignado. Contacte al administrador.",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            boolean isAdmin = isAdminOrSupport(authenticatedUser);

            // Obtener datos anteriores para auditoría
            Training oldTraining = trainingService.getTrainingEntityById(trainingId);

            // Verificar autorización: propietario, admin o support
            if (!isAdmin && !oldTraining.getUser().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado para actualizar este registro",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Actualizar registro
            TrainingResponseDTO response = trainingService.updateTraining(trainingId, userId, isAdmin, dto);

            // Registrar en auditoría
            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "trainings",
                    trainingId,
                    AuditLog.AuditAction.UPDATE,
                    oldTraining,
                    response,
                    "Actualización de capacitación: " + response.getCourseName(),
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registro de capacitación actualizado exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (TrainingNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );

        } catch (InvalidCompletionDateException | InvalidTrainingDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()
                    )
            );

        } catch (TrainingUnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        }
    }

    /**
     * DELETE - Eliminar un registro de capacitación
     * El propietario, ADMIN y SUPPORT pueden eliminar registros
     */
    @DeleteMapping("/{trainingId}")
    @Operation(
            summary = "Eliminar capacitación",
            description = "Elimina un registro de capacitación. El propietario, ADMIN y SUPPORT pueden eliminar"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para eliminar este registro"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteTraining(
            @Parameter(description = "ID del registro de capacitación", example = "1")
            @PathVariable Integer trainingId,
            HttpServletRequest request) {
        try {
            var authenticatedUser = getAuthenticatedUser();
            Integer userId = authenticatedUser.getUserId();

            // Validación segura: Verificar que el usuario tenga rol asignado
            if (authenticatedUser.getRole() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "Usuario sin rol asignado. Contacte al administrador.",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            boolean isAdmin = isAdminOrSupport(authenticatedUser);

            // Obtener datos antes de eliminar (para auditoría)
            Training training = trainingService.getTrainingEntityById(trainingId);

            // Verificar autorización: propietario, admin o support
            if (!isAdmin && !training.getUser().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado para eliminar este registro",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Eliminar registro
            trainingService.deleteTraining(trainingId, userId, isAdmin);

            // Registrar en auditoría
            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "trainings",
                    trainingId,
                    AuditLog.AuditAction.DELETE,
                    training,
                    null,
                    "Eliminación de capacitación: " + training.getCourseName(),
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registro de capacitación eliminado exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (TrainingNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );

        } catch (TrainingUnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN.value()
                    )
            );
        }
    }

    /**
     * GET - Obtener todos los registros de capacitación de cualquier usuario
     * Solo accesible por ADMIN y SUPPORT
     * Requiere enviar el userId como parámetro
     */
    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Obtener capacitaciones de un usuario (ADMIN/SUPPORT)",
            description = "Obtiene todos los registros de capacitación de cualquier usuario. Solo accesible por ADMIN y SUPPORT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN y SUPPORT"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<List<TrainingResponseDTO>>> getUserTrainings(
            @Parameter(description = "ID del usuario", example = "5")
            @PathVariable Integer userId) {
        try {
            var authenticatedUser = getAuthenticatedUser();
            boolean isAdmin = isAdminOrSupport(authenticatedUser);

            // Verificar que el usuario sea ADMIN o SUPPORT
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado - Solo administradores y soporte",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            List<TrainingResponseDTO> trainings = trainingService.getTrainingsByUserId(userId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Registros de capacitación obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            trainings
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
     * Helper para verificar si el usuario tiene rol de ADMIN o SUPPORT
     *
     * @param user Usuario a verificar
     * @return true si es ADMIN o SUPPORT, false en caso contrario
     */
    private boolean isAdminOrSupport(com.hse.Curriculum.Models.Users user) {
        if (user.getRole() == null) {
            return false;
        }
        String roleName = user.getRole().getName();
        return "ADMIN".equals(roleName) || "SUPPORT".equals(roleName);
    }
}
