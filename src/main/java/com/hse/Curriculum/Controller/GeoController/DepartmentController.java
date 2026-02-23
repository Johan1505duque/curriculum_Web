package com.hse.Curriculum.Controller.GeoController;
import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Dto.GeoDTO.Department.DepartmentCreateDTO;
import com.hse.Curriculum.Dto.GeoDTO.Department.DepartmentResponseDTO;
import com.hse.Curriculum.Dto.GeoDTO.Department.DepartmentUpdateDTO;
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
@RequestMapping("departments")
@Tag(name = "Department Management", description = "Gestión de departamentos por país")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class DepartmentController {

    private final GeoService   geoService;
    private final UsersService usersService;

    /**
     * POST - Crear departamento (solo ADMIN)
     */
    @PostMapping
    @Operation(
            summary     = "Crear departamento",
            description = "Crea un nuevo departamento asociado a un país. Solo accesible por ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Departamento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o departamento duplicado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "404", description = "País no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<DepartmentResponseDTO>> createDepartment(
            @Valid @RequestBody DepartmentCreateDTO dto) {
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

            DepartmentResponseDTO response = geoService.createDepartment(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "Departamento creado exitosamente",
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
     * GET - Obtener todos los departamentos de un país
     */
    @GetMapping("/country/{countryId}")
    @Operation(
            summary     = "Listar departamentos por país",
            description = "Retorna todos los departamentos de un país ordenados A-Z"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamentos obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "País no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<List<DepartmentResponseDTO>>> getDepartmentsByCountry(
            @Parameter(description = "ID del país", example = "39")
            @PathVariable Integer countryId) {
        try {
            getAuthenticatedUser();

            List<DepartmentResponseDTO> response =
                    geoService.getDepartmentsByCountry(countryId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Departamentos obtenidos exitosamente",
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
     * GET - Obtener un departamento por ID
     */
    @GetMapping("/{departmentId}")
    @Operation(
            summary     = "Obtener departamento por ID",
            description = "Retorna un departamento específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<DepartmentResponseDTO>> getDepartmentById(
            @Parameter(description = "ID del departamento", example = "1")
            @PathVariable Integer departmentId) {
        try {
            getAuthenticatedUser();

            DepartmentResponseDTO response =
                    geoService.getDepartmentById(departmentId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Departamento obtenido exitosamente",
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
     * PATCH - Actualizar departamento (solo ADMIN)
     */
    @PatchMapping("/{departmentId}")
    @Operation(
            summary     = "Actualizar departamento",
            description = "Actualiza parcialmente un departamento. Solo accesible por ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<DepartmentResponseDTO>> updateDepartment(
            @Parameter(description = "ID del departamento", example = "1")
            @PathVariable Integer departmentId,
            @Valid @RequestBody DepartmentUpdateDTO dto) {
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

            DepartmentResponseDTO response =
                    geoService.updateDepartment(departmentId, dto);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Departamento actualizado exitosamente",
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
     * DELETE - Eliminar departamento (solo ADMIN)
     */
    @DeleteMapping("/{departmentId}")
    @Operation(
            summary     = "Eliminar departamento",
            description = "Elimina un departamento por ID. Solo accesible por ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteDepartment(
            @Parameter(description = "ID del departamento", example = "1")
            @PathVariable Integer departmentId) {
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

            geoService.deleteDepartment(departmentId);

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Departamento eliminado exitosamente",
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
