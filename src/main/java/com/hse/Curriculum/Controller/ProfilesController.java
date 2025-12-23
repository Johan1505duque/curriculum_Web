package com.hse.Curriculum.Controller;


import com.hse.Curriculum.Dto.ProfileDTO.RegisterPerfileDTO;
import com.hse.Curriculum.Dto.ProfileDTO.ProfessionalProfileUpdateDTO;
import com.hse.Curriculum.Dto.ProfileDTO.ProfileResponseDTO;
import com.hse.Curriculum.Exception.Profile.*;
import com.hse.Curriculum.Service.ProfilesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("profiles")
@Tag(name = "Profiles", description = "Gestión de perfiles de usuario")
@CrossOrigin(origins = "*")
public class ProfilesController {
    private final ProfilesService profilesService;

    public ProfilesController(ProfilesService profilesService) {
        this.profilesService = profilesService;
    }

    /**
     * CREAR perfil para un usuario
     */
    @PostMapping("/user/{userId}/register")
    @Operation(summary = "Crear perfil de usuario",
            description = "Crea un perfil con datos personales para un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Perfil creado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "El usuario ya tiene perfil o documento duplicado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<?> createProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId,
            @Valid @RequestBody RegisterPerfileDTO profileDTO) {
        try {
            ProfileResponseDTO response = profilesService.createProfile(userId, profileDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ProfileAlreadyExistsException | DuplicateDocumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ACTUALIZAR perfil del usuario
     */
    @PutMapping("/user/{userId}")
    @Operation(summary = "Actualizar perfil de usuario",
            description = "Actualiza los datos personales del perfil de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "409", description = "Documento duplicado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<?> updateProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId,
            @Valid @RequestBody RegisterPerfileDTO profileDTO) {
        try {
            ProfileResponseDTO response = profilesService.updateProfile(userId, profileDTO);
            return ResponseEntity.ok(response);

        } catch (ProfileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (DuplicateDocumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
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
     * OBTENER perfil por ID de usuario
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener perfil de usuario",
            description = "Recupera el perfil de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    public ResponseEntity<?> getProfile(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId) {
        try {
            ProfileResponseDTO response = profilesService.getProfileByUserId(userId);
            return ResponseEntity.ok(response);

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


