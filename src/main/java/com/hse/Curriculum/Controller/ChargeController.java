package com.hse.Curriculum.Controller;
import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Dto.ChargeDTO.ChargeRegisterDTO;
import com.hse.Curriculum.Dto.ChargeDTO.ChargeResponseDTO;
import com.hse.Curriculum.Dto.ChargeDTO.ChargeUpdateDTO;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Models.Charge;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Service.AuditService;
import com.hse.Curriculum.Service.ChargeService;
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
import java.util.Map;

@RestController
@RequestMapping("charges")
@Tag(name = "Charge Management", description = "Endpoints para gestionar cargos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ChargeController {

    private final ChargeService postService;
    private final AuditService auditService;
    private final UsersService usersService;

    /**
     * POST - Registrar nuevo cargo
     */
    @PostMapping("/register")
    @Operation(
            summary = "Registrar cargo",
            description = "Crea un nuevo cargo en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cargo registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Nombre de cargo ya existe"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<ChargeResponseDTO>> register(
            @Valid @RequestBody ChargeRegisterDTO registerDTO,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedEmail = authentication.getName();
            Users authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Registrar cargo
            Charge post = postService.register(registerDTO, authenticatedUser.getUserId());

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "post",
                    post.getChargeId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    post,
                    "Se ha registrado un nuevo cargo",
                    request
            );

            // Preparar respuesta
            ChargeResponseDTO response = new ChargeResponseDTO();
            response.setChargeId(post.getChargeId());
            response.setNameCharge(post.getNameCharge());
            response.setDescription(post.getDescription());
            response.setStatus(post.getStatus());
            response.setCreatedAt(post.getCreatedAt());
            response.setUpdatedAt(post.getUpdatedAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "Cargo registrado exitosamente",
                            HttpStatus.CREATED.value(),
                            response
                    )
            );

        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("ya existe")) {
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
     * GET - Obtener cargo por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cargo por ID",
            description = "Busca un cargo específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cargo encontrado"),
            @ApiResponse(responseCode = "404", description = "Cargo no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<ChargeResponseDTO>> getById(
            @Parameter(description = "ID del cargo", example = "1")
            @PathVariable Integer id) {

        try {
            Charge post = postService.getById(id);

            ChargeResponseDTO response = new ChargeResponseDTO();
            response.setChargeId(post.getChargeId());
            response.setNameCharge(post.getNameCharge());
            response.setDescription(post.getDescription());
            response.setStatus(post.getStatus());
            response.setCreatedAt(post.getCreatedAt());
            response.setUpdatedAt(post.getUpdatedAt());

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Cargo encontrado",
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
     * GET - Listar todos los cargos activos
     */
    @GetMapping("/active")
    @Operation(summary = "Listar cargos activos",
            description = "Obtiene todos los cargos activos del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<List<ChargeResponseDTO>>> getAllActive() {
        List<ChargeResponseDTO> posts = postService.getAllActive();

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Cargos activos obtenidos exitosamente",
                        HttpStatus.OK.value(),
                        posts
                )
        );
    }

    /**
     * GET - Listar todos los cargos
     */
    @GetMapping
    @Operation(summary = "Listar todos los cargos",
            description = "Obtiene todos los cargos (activos e inactivos)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<List<ChargeResponseDTO>>> getAll() {
        List<ChargeResponseDTO> posts = postService.getAll();

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Cargos obtenidos exitosamente",
                        HttpStatus.OK.value(),
                        posts
                )
        );
    }

    /**
     * GET - Buscar cargos por nombre
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar cargos por nombre",
            description = "Busca cargos que contengan el texto especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda completada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<List<ChargeResponseDTO>>> searchByName(
            @Parameter(description = "Texto a buscar", example = "Director")
            @RequestParam String name) {

        List<ChargeResponseDTO> posts = postService.searchByName(name);

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Búsqueda completada",
                        HttpStatus.OK.value(),
                        posts
                )
        );
    }

    /**
     * PATCH - Actualizar cargo
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar cargo",
            description = "Actualiza los datos de un cargo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cargo actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cargo no encontrado"),
            @ApiResponse(responseCode = "409", description = "Nombre de cargo ya existe"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<ChargeResponseDTO>> update(
            @Parameter(description = "ID del cargo", example = "1")
            @PathVariable Integer id,
            @Valid @RequestBody ChargeUpdateDTO updateDTO,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedEmail = authentication.getName();
            Users authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Obtener cargo anterior para auditoría
            Charge oldPost = postService.getById(id);

            // Actualizar
            Charge updatedPost = postService.update(id, updateDTO, authenticatedUser.getUserId());

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "post",
                    id,
                    AuditLog.AuditAction.UPDATE,
                    oldPost,
                    updatedPost,
                    "Cargo actualizado",
                    request
            );

            // Preparar respuesta
            ChargeResponseDTO response = new ChargeResponseDTO();
            response.setChargeId(updatedPost.getChargeId());
            response.setNameCharge(updatedPost.getNameCharge());
            response.setDescription(updatedPost.getDescription());
            response.setStatus(updatedPost.getStatus());
            response.setCreatedAt(updatedPost.getCreatedAt());
            response.setUpdatedAt(updatedPost.getUpdatedAt());

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Cargo actualizado exitosamente",
                            HttpStatus.OK.value(),
                            response
                    )
            );

        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponseDTO.error(
                                e.getMessage(),
                                HttpStatus.NOT_FOUND.value()
                        )
                );
            }
            if (e.getMessage().contains("ya existe")) {
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
     * PATCH - Deshabilitar cargo
     */
    @PatchMapping("/{id}/disable")
    @Operation(summary = "Deshabilitar cargo",
            description = "Deshabilita un cargo del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cargo deshabilitado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cargo no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> disable(
            @Parameter(description = "ID del cargo", example = "1")
            @PathVariable Integer id,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedEmail = authentication.getName();
            Users authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Deshabilitar
            postService.disable(id, authenticatedUser.getUserId());

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "post",
                    id,
                    AuditLog.AuditAction.DISABLE,
                    Map.of("status", true),
                    Map.of("status", false),
                    "Cargo deshabilitado",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Cargo deshabilitado exitosamente",
                            HttpStatus.OK.value(),
                            null
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
     * PATCH - Habilitar cargo
     */
    @PatchMapping("/{id}/enable")
    @Operation(summary = "Habilitar cargo",
            description = "Reactiva un cargo deshabilitado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cargo habilitado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cargo no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<Void>> enable(
            @Parameter(description = "ID del cargo", example = "1")
            @PathVariable Integer id,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedEmail = authentication.getName();
            Users authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Habilitar
            postService.enable(id, authenticatedUser.getUserId());

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "post",
                    id,
                    AuditLog.AuditAction.ENABLE,
                    Map.of("status", false),
                    Map.of("status", true),
                    "Cargo habilitado",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Cargo habilitado exitosamente",
                            HttpStatus.OK.value(),
                            null
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
}
