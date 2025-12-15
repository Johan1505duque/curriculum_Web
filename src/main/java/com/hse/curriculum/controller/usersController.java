package com.hse.curriculum.controller;

import com.hse.curriculum.models.users;
import com.hse.curriculum.Service.usersService;
import com.hse.curriculum.dto.userRegisterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("users")
@Tag(name = "Gestión de Usuarios", description = "Endpoints para crear y consultar usuarios")
@CrossOrigin(origins = "*")
public class usersController {

    @Autowired
    private usersService usersService;

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> getUserById(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer id) {

        Optional<users> user = usersService.findById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Usuario no encontrado con ID: " + id,
                            "timestamp", java.time.LocalDateTime.now().toString()
                    ));
        }
    }

    /**
     * Buscar usuario por email
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuario por email", description = "Busca un usuario por su correo electrónico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> getUserByEmail(
            @Parameter(description = "Email del usuario", example = "juan@email.com")
            @PathVariable String email) {

        Optional<users> user = usersService.findByEmail(email);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Usuario no encontrado con email: " + email,
                            "timestamp", java.time.LocalDateTime.now().toString()
                    ));
        }
    }

    /**
     * Registrar nuevo usuario
     */
    @PostMapping
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email duplicado")
    })
    public ResponseEntity<?> createUser(@RequestBody userRegisterDTO dto) {
        try {
            users newUser = usersService.save(
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getEmail(),
                    dto.getPassword()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", e.getMessage(),
                            "timestamp", java.time.LocalDateTime.now().toString()
                    ));
        }
    }
}