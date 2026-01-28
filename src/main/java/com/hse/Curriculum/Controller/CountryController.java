package com.hse.Curriculum.Controller;

import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Dto.CountryDTO.*;
import com.hse.Curriculum.Exception.Country.CountryAlreadyExistsException;
import com.hse.Curriculum.Exception.Country.CountryInUseException;
import com.hse.Curriculum.Exception.Country.CountryNotFoundException;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Models.Country;
import com.hse.Curriculum.Service.AuditService;
import com.hse.Curriculum.Service.CountryService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
@Tag(name = "Country Management", description = "Gestión de países del sistema")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
public class CountryController {

    private final CountryService countryService;
    private final UsersService usersService;
    private final AuditService auditService;

    /**
     * GET - Obtener todos los países
     * Acceso público (sin autenticación)
     */
    @GetMapping
    @Operation(
            summary = "Obtener todos los países",
            description = "Obtiene el listado completo de países ordenados alfabéticamente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Países obtenidos exitosamente")
    })
    public ResponseEntity<ApiResponseDTO<List<CountryResponseDTO>>> getAllCountries() {
        log.info("GET /api/countries - Obteniendo todos los países");
        List<CountryResponseDTO> countries = countryService.getAllCountries();
        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Países obtenidos exitosamente",
                        HttpStatus.OK.value(),
                        countries
                )
        );
    }

    /**
     * GET - Obtener países con paginación
     * Acceso público (sin autenticación)
     */
    @GetMapping("/paginated")
    @Operation(
            summary = "Obtener países con paginación",
            description = "Obtiene países con soporte de paginación y ordenamiento"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Países obtenidos exitosamente")
    })
    public ResponseEntity<ApiResponseDTO<Page<CountryResponseDTO>>> getAllCountriesPaginated(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de elementos por página", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo por el cual ordenar", example = "countryName")
            @RequestParam(defaultValue = "countryName") String sortBy) {
        log.info("GET /api/countries/paginated - página: {}, tamaño: {}", page, size);
        Page<CountryResponseDTO> countries = countryService.getAllCountries(page, size, sortBy);
        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Países obtenidos exitosamente",
                        HttpStatus.OK.value(),
                        countries
                )
        );
    }



    /**
     * GET - Obtener país por ID
     * Acceso público (sin autenticación)
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener país por ID",
            description = "Obtiene la información completa de un país por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "País obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "País no encontrado")
    })
    public ResponseEntity<ApiResponseDTO<CountryResponseDTO>> getCountryById(
            @Parameter(description = "ID del país", example = "1")
            @PathVariable Integer id) {
        try {
            log.info("GET /api/countries/{} - Obteniendo país por ID", id);
            CountryResponseDTO country = countryService.getCountryById(id);
            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "País obtenido exitosamente",
                            HttpStatus.OK.value(),
                            country
                    )
            );
        } catch (CountryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );
        }
    }




    /**
     * GET - Buscar países por nombre
     * Acceso público (sin autenticación)
     */
    @GetMapping("/search")
    @Operation(
            summary = "Buscar países por nombre",
            description = "Busca países cuyo nombre contenga el texto especificado (case-insensitive)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    })
    public ResponseEntity<ApiResponseDTO<List<CountryResponseDTO>>> searchCountriesByName(
            @Parameter(description = "Texto a buscar en el nombre del país", example = "col")
            @RequestParam String name) {
        log.info("GET /api/countries/search?name={}", name);
        List<CountryResponseDTO> countries = countryService.searchCountriesByName(name);
        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Búsqueda realizada exitosamente",
                        HttpStatus.OK.value(),
                        countries
                )
        );
    }



    /**
     * POST - Crear un nuevo país (SOLO ADMIN)
     */
    @PostMapping
    @Operation(
            summary = "Crear nuevo país (ADMIN)",
            description = "Crea un nuevo país en el sistema. Solo accesible por administradores"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "País creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "409", description = "País ya existe")
    })
    public ResponseEntity<ApiResponseDTO<CountryResponseDTO>> createCountry(
            @Valid @RequestBody CountryCreateDTO dto,
            HttpServletRequest request) {
        try {
            var authenticatedUser = getAuthenticatedUser();

            // Verificar que sea ADMIN
            if (!authenticatedUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado - Solo administradores pueden crear países",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            log.info("POST /api/countries - Creando nuevo país: {}", dto.getCountryName());
            CountryResponseDTO country = countryService.createCountry(dto);

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "countries",
                    country.getCountryId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    country,
                    "Creación de país: " + country.getCountryName(),
                    request
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "País creado exitosamente",
                            HttpStatus.CREATED.value(),
                            country
                    )
            );

        } catch (CountryAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.CONFLICT.value()
                    )
            );
        }
    }

    /**
     * PATCH - Actualizar un país (SOLO ADMIN)
     */
    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar país (ADMIN)",
            description = "Actualiza parcialmente un país existente. Solo accesible por administradores"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "País actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "404", description = "País no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes")
    })
    public ResponseEntity<ApiResponseDTO<CountryResponseDTO>> updateCountry(
            @Parameter(description = "ID del país", example = "1")
            @PathVariable Integer id,
            @Valid @RequestBody CountryUpdateDTO dto,
            HttpServletRequest request) {
        try {
            var authenticatedUser = getAuthenticatedUser();

            // Verificar que sea ADMIN
            if (!authenticatedUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado - Solo administradores pueden actualizar países",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Obtener datos anteriores para auditoría
            CountryResponseDTO oldCountry = countryService.getCountryById(id);

            log.info("PATCH /api/countries/{} - Actualizando país", id);
            CountryResponseDTO country = countryService.updateCountry(id, dto);

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "countries",
                    id,
                    AuditLog.AuditAction.UPDATE,
                    oldCountry,
                    country,
                    "Actualización de país: " + country.getCountryName(),
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "País actualizado exitosamente",
                            HttpStatus.OK.value(),
                            country
                    )
            );

        } catch (CountryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );

        } catch (CountryAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.CONFLICT.value()
                    )
            );
        }
    }

    /**
     * DELETE - Eliminar un país (SOLO ADMIN)
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar país (ADMIN)",
            description = "Elimina un país del sistema. Solo accesible por administradores"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "País eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN"),
            @ApiResponse(responseCode = "404", description = "País no encontrado"),
            @ApiResponse(responseCode = "409", description = "País en uso, no se puede eliminar")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteCountry(
            @Parameter(description = "ID del país", example = "1")
            @PathVariable Integer id,
            HttpServletRequest request) {
        try {
            var authenticatedUser = getAuthenticatedUser();

            // Verificar que sea ADMIN
            if (!authenticatedUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponseDTO.error(
                                "No autorizado - Solo administradores pueden eliminar países",
                                HttpStatus.FORBIDDEN.value()
                        )
                );
            }

            // Obtener datos antes de eliminar (para auditoría)
            CountryResponseDTO country = countryService.getCountryById(id);

            log.info("DELETE /api/countries/{} - Eliminando país", id);
            countryService.deleteCountry(id);

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "countries",
                    id,
                    AuditLog.AuditAction.DELETE,
                    country,
                    null,
                    "Eliminación de país: " + country.getCountryName(),
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "País eliminado exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (CountryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND.value()
                    )
            );

        } catch (CountryInUseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.CONFLICT.value()
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