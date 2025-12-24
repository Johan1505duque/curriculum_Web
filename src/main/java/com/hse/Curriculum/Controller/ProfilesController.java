package com.hse.Curriculum.Controller;

import com.hse.Curriculum.Dto.ProfileDTO.*;
import com.hse.Curriculum.Exception.Profile.*;
import com.hse.Curriculum.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("profiles")
@Tag(name = "Profiles", description = "Gestión de perfiles de usuario")
@CrossOrigin(origins = "*")
public class ProfilesController {

    private final ProfilesService profilesService;
    private final UserProfileService userProfileService; // ✅ Un solo servicio con todos los métodos

    // ✅ Constructor correcto
    public ProfilesController(ProfilesService profilesService,
                              UserProfileService userProfileService) {
        this.profilesService = profilesService;
        this.userProfileService = userProfileService;
    }

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
    public ResponseEntity<?> registerComplete(
            @Valid @RequestBody PerfileRegisterDTO registrationDTO) {
        try {
            // ✅ Usar el mismo servicio que ya tienes inyectado
            ProfileResponseDTO response =
                    userProfileService.registerUserWithProfile(registrationDTO);

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
     */
    @PutMapping("/{userId}/complete")
    @Operation(
            summary = "Actualizar usuario y perfil completo",
            description = "Actualiza nombre, apellido y datos del perfil. Email NO modificable."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario o perfil no encontrado"),
            @ApiResponse(responseCode = "409", description = "Documento duplicado")
    })
    public ResponseEntity<?> updateCompleteProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId,
            @Valid @RequestBody ProfileUpdateDTO updateDTO) {
        try {
            // ✅ Mismo servicio, método diferente
            ProfileResponseDTO response =
                    userProfileService.updateCompleteProfile(userId, updateDTO);

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
     */
    @GetMapping("/{userId}/complete")
    @Operation(summary = "Obtener información completa del usuario",
            description = "Retorna datos de usuario y perfil combinados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información obtenida"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> getUserProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId) {
        try {
            // ✅ Mismo servicio
            UserProfileDTO response = userProfileService.getUserProfile(userId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ACTUALIZAR información profesional del perfil
     */
    @PatchMapping("/user/{userId}/professional-info")
    @Operation(
            summary = "Actualizar información profesional",
            description = "Actualiza únicamente el resumen y los logros profesionales del perfil"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Información profesional actualizada"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<?> updateProfessionalInfo(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId,
            @Valid @RequestBody ProfessionalProfileUpdateDTO dto
    ) {
        try {
            profilesService.updateProfessionalInfo(userId, dto);
            return ResponseEntity.noContent().build();

        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }



    /**
     * ELIMINAR perfil
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Eliminar perfil de usuario",
            description = "Elimina el perfil de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    public ResponseEntity<?> deleteProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId) {
        try {
            profilesService.deleteProfile(userId);
            return ResponseEntity.noContent().build();

        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}