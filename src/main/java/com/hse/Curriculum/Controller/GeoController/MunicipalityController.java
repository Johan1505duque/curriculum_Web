package com.hse.Curriculum.Controller.GeoController;
import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Dto.GeoDTO.Municipality.*;
import com.hse.Curriculum.Service.GeoService;
import com.hse.Curriculum.Service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("municipalities")
@Tag(name = "Municipality Management", description = "Gestión de municipios por departamento")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor

public class MunicipalityController {
    private final GeoService   geoService;
    private final UsersService usersService;

    /**
     * POST - Crear municipio (solo ADMIN)
     */
    @PostMapping
    @Operation(
            summary     = "Crear municipio",
            description = "Crea un nuevo municipio asociado a un departamento. Solo accesible por ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Municipio creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o municipio duplicado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<MunicipalityResponseDTO>> createMunicipality(
            @Valid @RequestBody MunicipalityCreateDTO dto) {
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

            MunicipalityResponseDTO response = geoService.createMunicipality(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "Municipio creado exitosamente",
                            HttpStatus.CREATED.value(),
                            response
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        }
    }

    /**
     * GET - Obtener todos los municipios de un departamento
     */
    @GetMapping("/department/{departmentId}")
    @Operation(
            summary     = "Listar municipios por departamento",
            description = "Retorna todos los municipios de un departamento ordenados A-Z. La capital aparece con isCapital=true"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Municipios obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<List<MunicipalityResponseDTO>>> getMunicipalitiesByDepartment(
            @Parameter(description = "ID del departamento", example = "1")
            @PathVariable Integer departmentId) {
        try {
            getAuthenticatedUser();

            List<MunicipalityResponseDTO> response =
                    geoService.getMunicipalitiesByDepartment(departmentId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Municipios obtenidos exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    /**
     * GET - Obtener municipio por ID
     */
    @GetMapping("/{municipalityId}")
    @Operation(
            summary     = "Obtener municipio por ID",
            description = "Retorna un municipio específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Municipio obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Municipio no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<MunicipalityResponseDTO>> getMunicipalityById(
            @Parameter(description = "ID del municipio", example = "1")
            @PathVariable Integer municipalityId) {
        try {
            getAuthenticatedUser();

            MunicipalityResponseDTO response =
                    geoService.getMunicipalityById(municipalityId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Municipio obtenido exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    /**
     * GET - Obtener capital de un departamento
     */
    @GetMapping("/capital/{departmentId}")
    @Operation(
            summary     = "Obtener capital de un departamento",
            description = "Retorna el municipio marcado como capital del departamento indicado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capital obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Capital no encontrada")
    })
    public ResponseEntity<ApiResponseDTO<MunicipalityResponseDTO>> getCapitalByDepartment(
            @Parameter(description = "ID del departamento", example = "1")
            @PathVariable Integer departmentId) {
        try {
            getAuthenticatedUser();

            MunicipalityResponseDTO response =
                    geoService.getCapitalByDepartment(departmentId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Capital obtenida exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    /**
     * PATCH - Actualizar municipio (solo ADMIN)
     */
    @PatchMapping("/{municipalityId}")
    @Operation(
            summary     = "Actualizar municipio",
            description = "Actualiza parcialmente un municipio. Si se cambia la capital se desmarca la anterior. Solo accesible por ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Municipio actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Municipio no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<MunicipalityResponseDTO>> updateMunicipality(
            @Parameter(description = "ID del municipio", example = "1")
            @PathVariable Integer municipalityId,
            @Valid @RequestBody MunicipalityUpdateDTO dto) {
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

            MunicipalityResponseDTO response =
                    geoService.updateMunicipality(municipalityId, dto);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Municipio actualizado exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    /**
     * DELETE - Eliminar municipio (solo ADMIN)
     */
    @DeleteMapping("/{municipalityId}")
    @Operation(
            summary     = "Eliminar municipio",
            description = "Elimina un municipio por ID. Solo accesible por ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Municipio eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Municipio no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteMunicipality(
            @Parameter(description = "ID del municipio", example = "1")
            @PathVariable Integer municipalityId) {
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

            geoService.deleteMunicipality(municipalityId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Municipio eliminado exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value())
            );
        }
    }

    // ── Helper JWT ───────────────────────────────────────────────
    private com.hse.Curriculum.Models.Users getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String email = authentication.getName();
        return usersService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario autenticado no encontrado"));
    }
}
