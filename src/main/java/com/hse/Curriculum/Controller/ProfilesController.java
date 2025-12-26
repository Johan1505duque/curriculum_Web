package com.hse.Curriculum.Controller;


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
@Tag(name = "Profiles", description = "Gestión de perfiles de usuario")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProfilesController {

    private final ProfilesService profilesService;
    private final UserProfileService userProfileService;
    private final AuditService auditService;
    private final UsersService usersService;

    /**
     * POST - Registrar usuario con perfil completo
     * NO REQUIERE AUTENTICACIÓN
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
    public ResponseEntity<?> registerComplete(
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

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DuplicateDocumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error", "Documento duplicado",
                            "message", e.getMessage()
                    ));

        } catch (RuntimeException e) {
            if (e.getMessage().contains("email")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "error", "Email duplicado",
                                "message", e.getMessage()
                        ));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Error en el registro",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * PUT - Actualizar datos completos
     * REQUIERE AUTENTICACIÓN
     */
    @PutMapping("/{userId}/complete")
    @Operation(
            summary = "Actualizar usuario y perfil completo",
            description = "Actualiza nombre, apellido y datos del perfil. Email NO modificable."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario o perfil no encontrado"),
            @ApiResponse(responseCode = "409", description = "Documento duplicado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<?> updateCompleteProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId,
            @Valid @RequestBody ProfileUpdateDTO updateDTO,
            HttpServletRequest request) {
        try {
            // Verificar autorización
            String authenticatedEmail = getAuthenticatedUserEmail();
            var authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            if (!authenticatedUser.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No autorizado para actualizar este perfil"));
            }

            // Obtener datos anteriores para auditoría
            UserProfileDTO oldData = userProfileService.getUserProfile(userId);

            // Actualizar perfil
            ProfileResponseDTO response =
                    userProfileService.updateCompleteProfile(userId, updateDTO);

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

            return ResponseEntity.ok(response);

        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Perfil no encontrado", "message", e.getMessage()));

        } catch (DuplicateDocumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Documento duplicado", "message", e.getMessage()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado", "message", e.getMessage()));
        }
    }

    /**
     * GET - Obtener información completa
     * REQUIERE AUTENTICACIÓN
     */
    @GetMapping("/{userId}/complete")
    @Operation(summary = "Obtener información completa del usuario",
            description = "Retorna datos de usuario y perfil combinados")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información obtenida"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> getUserProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId) {
        try {
            UserProfileDTO response = userProfileService.getUserProfile(userId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ACTUALIZAR información profesional del perfil
     * REQUIERE AUTENTICACIÓN
     */
    @PatchMapping("/user/{userId}/professional-info")
    @Operation(
            summary = "Actualizar información profesional",
            description = "Actualiza únicamente el resumen y los logros profesionales del perfil"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Información profesional actualizada"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<?> updateProfessionalInfo(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId,
            @Valid @RequestBody ProfessionalProfileUpdateDTO dto,
            HttpServletRequest request
    ) {
        try {
            // Verificar autorización
            String authenticatedEmail = getAuthenticatedUserEmail();
            var authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            if (!authenticatedUser.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No autorizado"));
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
                    Map.of("summary", profile.setProfessionalSummary(),
                            "achievements", profile.getCareerAchievements()),
                    dto,
                    "Actualización de información profesional",
                    request
            );

            return ResponseEntity.noContent().build();

        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ELIMINAR perfil
     * REQUIERE AUTENTICACIÓN
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Eliminar perfil de usuario",
            description = "Elimina el perfil de un usuario")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<?> deleteProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId,
            HttpServletRequest request) {
        try {
            // Verificar autorización
            String authenticatedEmail = getAuthenticatedUserEmail();
            var authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            if (!authenticatedUser.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No autorizado"));
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

            return ResponseEntity.noContent().build();

        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
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