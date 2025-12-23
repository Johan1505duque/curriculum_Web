package com.hse.Curriculum.Controller;

import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Service.UsersService;
import com.hse.Curriculum.Dto.UserDTO.UserResponseDTO;
import com.hse.Curriculum.Dto.UserDTO.UserSignUpDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("users")
@Tag(name = "Gestión de Usuarios", description = "Endpoints para crear y consultar usuarios")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario",
            description = "Crea un nuevo usuario con datos básicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    public ResponseEntity<UserResponseDTO> register(
            @Valid @RequestBody UserSignUpDTO signUpDTO
    ) {
        UserResponseDTO response = usersService.register(signUpDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * OBTENER usuario por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID",
            description = "Busca un usuario específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Users> getUserById(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer id) {

        Optional<Users> user = usersService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DESHABILITAR usuario (soft delete)
     */
    @PatchMapping("/{id}/disable")
    @Operation(summary = "Deshabilitar usuario",
            description = "Deshabilita un usuario del sistema sin eliminarlo permanentemente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario deshabilitado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> disableUser(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer id) {
        try {
            usersService.disableUser(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario deshabilitado exitosamente");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * HABILITAR usuario
     */
    @PatchMapping("/{id}/enable")
    @Operation(summary = "Habilitar usuario",
            description = "Reactiva un usuario deshabilitado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario habilitado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> enableUser(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer id) {
        try {
            usersService.enableUser(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario habilitado exitosamente");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}