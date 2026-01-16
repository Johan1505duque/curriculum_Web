package com.hse.Curriculum.Controller;

import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Dto.ProfileDTO.*;
import com.hse.Curriculum.Exception.Profile.*;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Service.*;
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

import java.util.Map;

@RestController
@RequestMapping("profiles")
@Tag(name = "Profiles Management", description = "Gestión de perfiles de usuario")
@RequiredArgsConstructor
public class ProfilesController {

    private final ProfilesService profilesService;
    private final UserProfileService userProfileService;
    private final AuditService auditService;
    private final UsersService usersService;

    /**
     * POST - Registrar usuario con perfil completo
     */
    @PostMapping("/complete")
    @Operation(
            summary = "Registro completo de usuario",
            description = "Crea un usuario con sus datos de autenticación y perfil personal en una sola operación"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario y perfil creados exitosamente"),
            @ApiResponse(responseCode = "409", description = "Email o documento ya registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ApiResponseDTO<ProfileResponseDTO>> registerComplete(
            @Valid @RequestBody PerfileRegisterDTO registrationDTO,
            HttpServletRequest request) {
        try {
            ProfileResponseDTO response =
                    userProfileService.registerUserWithProfile(registrationDTO);

            // Registrar creación en auditoría
            auditService.logAction(
                    response.getUserId(),
                    registrationDTO.getEmail(),
                    registrationDTO.getFirstName() + " " + registrationDTO.getLastName(),
                    "profiles",
                    response.getProfileId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    response,
                    "Registro completo de usuario y perfil",
                    request
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "Usuario y perfil creados exitosamente",
                            HttpStatus.CREATED.value(),
                            response
                    )
            );

        } catch (DuplicateDocumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ApiResponseDTO.error(
                             e.getMessage(),
                            HttpStatus.CONFLICT.value()
                    )
            );

        } catch (RuntimeException e) {
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
     * PUT - Actualizar datos completos
     */
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{userId}/complete")
    @Operation(
            summary = "Actualizar usuario y perfil (parcial)",
            description = "Actualiza solo los campos enviados del usuario y perfil. Email NO modificable."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario o perfil no encontrado"),
            @ApiResponse(responseCode = "409", description = "Documento duplicado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ApiResponseDTO<ProfileResponseDTO>> updateCompleteProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId,
            @RequestBody ProfileUpdateDTO updateDTO,
            HttpServletRequest request) {
        try {
            // Verificar autorización
            String authenticatedEmail = getAuthenticatedUserEmail();
            var authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            if (!authenticatedUser.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado para actualizar este perfil",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Obtener datos anteriores para auditoría
            UserProfileDTO oldData = userProfileService.getUserProfile(userId);

            // Actualizar perfil
            ProfileResponseDTO response =
                    userProfileService.patchCompleteProfile(userId, updateDTO);

            // Registrar actualización en auditoría
            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "profiles",
                    response.getProfileId(),
                    AuditLog.AuditAction.UPDATE,
                    oldData,
                    response,
                    "Actualización de perfil completo",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Perfil actualizado exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );

        } catch (DuplicateDocumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.CONFLICT.value()
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
     * GET - Obtener información completa
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{userId}/complete")
    @Operation(summary = "Obtener información completa del usuario",
            description = "Retorna datos de usuario y perfil combinados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información obtenida"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<UserProfileDTO>> getUserProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId) {
        try {
            UserProfileDTO response = userProfileService.getUserProfile(userId);
            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Información de perfil obtenida exitosamente",
                            HttpStatus.OK.value(),
                            response
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
     * ACTUALIZAR información profesional del perfil
     */
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/user/{userId}/professional-info")
    @Operation(
            summary = "Actualizar información profesional",
            description = "Actualiza únicamente el resumen y los logros profesionales del perfil"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Información profesional actualizada"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ApiResponseDTO<Void>> updateProfessionalInfo(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId,
            @Valid @RequestBody ProfessionalProfileUpdateDTO dto,
            HttpServletRequest request) {
        try {
            // Verificar autorización
            String authenticatedEmail = getAuthenticatedUserEmail();
            var authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            if (!authenticatedUser.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Obtener perfil anterior
            var profile = profilesService.getProfileByUserId(userId);

            // Actualizar
            profilesService.updateProfessionalInfo(userId, dto);

            // Registrar en auditoría
            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "profiles",
                    profile.getProfilesId(),
                    AuditLog.AuditAction.UPDATE,
                    Map.of("summary", profile.getProfessionalSummary(),
                            "achievements", profile.getCareerAchievements()),
                    dto,
                    "Actualización de información profesional",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Información profesional actualizada exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
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
     * OBTENER información profesional del perfil
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user/{userId}/professional-info")
    @Operation(
            summary = "Obtener información profesional",
            description = "Obtiene el resumen y los logros profesionales del perfil"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información profesional obtenida"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ApiResponseDTO<ProfessionalProfileResponseDTO>> getProfessionalInfo(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId) {
        try {
            // Verificar autorización
            String authenticatedEmail = getAuthenticatedUserEmail();
            var authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            if (!authenticatedUser.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Obtener información profesional
            ProfessionalProfileResponseDTO response =
                    userProfileService.getProfessionalInfo(userId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Información profesional obtenida exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                             e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
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
     * ELIMINAR perfil
     */
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Eliminar perfil de usuario",
            description = "Elimina el perfil de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId,
            HttpServletRequest request) {
        try {
            // Verificar autorización
            String authenticatedEmail = getAuthenticatedUserEmail();
            var authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            if (!authenticatedUser.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Obtener perfil antes de eliminar
            var profile = profilesService.getProfileByUserId(userId);

            // Eliminar
            profilesService.deleteProfile(userId);

            // Registrar en auditoría
            auditService.logAction(
                    userId,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "profiles",
                    profile.getProfilesId(),
                    AuditLog.AuditAction.DELETE,
                    profile,
                    null,
                    "Eliminación de perfil",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Perfil eliminado exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
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
     * Helper para obtener el email del usuario autenticado
     */
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        return authentication.getName();
    }
}