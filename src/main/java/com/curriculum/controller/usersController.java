package com.curriculum.controller;
import com.curriculum.Service.usersService;
import com.curriculum.models.users;
import com.curriculum.dto.userRegisterDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "Gestión de Usuarios", description = "Endpoints para crear, consultar y gestionar usuarios")
@CrossOrigin(origins = "*")
public class usersController {

    private final usersService usersService;

    public usersController() {
        this.usersService = new usersService();
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Busca y retorna la información completa de un usuario específico mediante su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = users.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido proporcionado")
    })
    public ResponseEntity<?> getUserById(
            @Parameter(description = "ID único del usuario", required = true, example = "1")
            @PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse("El ID debe ser un número positivo"));
        }

        users user = usersService.getUserById(id);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Usuario con ID " + id + " no encontrado"));
        }
    }

    /**
     * Registrar nuevo usuario
     */
    @PostMapping
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema con los datos proporcionados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = users.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos"),
            @ApiResponse(responseCode = "500", description = "Error interno al crear el usuario")
    })
    public ResponseEntity<?> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(schema = @Schema(implementation = userRegisterDTO.class))
            )
            @RequestBody userRegisterDTO userDTO) {

        if (userDTO == null) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse("Los datos del usuario son requeridos"));
        }

        users newUser = usersService.registerUser(
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                userDTO.getPassword()
        );

        if (newUser != null) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(newUser);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Error al registrar usuario. Verifique los datos"));
        }
    }

    /**
     * Verificar si usuario existe
     */
    @GetMapping("/exists/{id}")
    @Operation(
            summary = "Verificar existencia de usuario",
            description = "Comprueba si existe un usuario con el ID proporcionado en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación completada exitosamente")
    })
    public ResponseEntity<Map<String, Object>> checkUserExists(
            @Parameter(description = "ID del usuario a verificar", required = true, example = "1")
            @PathVariable int id) {

        boolean exists = usersService.userExists(id);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", id);
        response.put("exists", exists);
        response.put("message", exists ? "El usuario existe" : "El usuario no existe");

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(
            summary = "Health Check",
            description = "Verifica que el servicio de usuarios está funcionando correctamente"
    )
    @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Users API");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Método auxiliar para crear respuestas de error consistentes
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return error;
    }
}

