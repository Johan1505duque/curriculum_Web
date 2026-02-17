package com.hse.Curriculum.Controller;

import com.hse.Curriculum.Dto.ApiResponseDTO;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Models.Profiles;
import com.hse.Curriculum.Service.AuditService;
import com.hse.Curriculum.Service.UsersService;
import com.hse.Curriculum.Service.ProfilesService;
import com.hse.Curriculum.Dto.UserDTO.UserSignUpDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("users")
@Tag(name = "User Management", description = "Endpoints para crear y consultar usuarios")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    private final AuditService auditService;
    private final ProfilesService profilesService;

    /**
     * POST - Registrar usuario básico con perfil automático
     * NO REQUIERE AUTENTICACIÓN
     */
    @PostMapping("/register")
    @Operation(
            summary = "Registrar usuario",
            description = "Crea un nuevo usuario con datos básicos y un perfil vacío listo para completar"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    public ResponseEntity<ApiResponseDTO<UserSignUpDTO>> register(
            @Valid @RequestBody UserSignUpDTO signUpDTO,
            HttpServletRequest request) {
        try {
            // 1. Registrar usuario
            Users user = usersService.register(signUpDTO);

            // 2. Crear perfil vacío automáticamente
            Profiles profile = profilesService.createEmptyProfileForUser(user.getUserId());

            // 3. Registrar en auditoría
            auditService.logAction(
                    user.getUserId(),
                    user.getEmail(),
                    user.getFirstName() + " " + user.getLastName(),
                    "users",
                    user.getUserId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    user,
                    "Se ha registrado un nuevo usuario",
                    request
            );

            // También registrar creación del perfil
            auditService.logAction(
                    user.getUserId(),
                    user.getEmail(),
                    user.getFirstName() + " " + user.getLastName(),
                    "profiles",
                    profile.getProfilesId(),
                    AuditLog.AuditAction.INSERT,
                    null,
                    profile,
                    "Perfil inicial creado automáticamente",
                    request
            );

            // 4. Respuesta exitosa
            UserSignUpDTO response = new UserSignUpDTO(
                    user.getUserId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    "Usuario registrado exitosamente."
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDTO.success(
                            "Usuario registrado exitosamente",
                            HttpStatus.CREATED.value(),
                            response
                    )
            );

        } catch (RuntimeException e) {
            // Manejar email duplicado u otros errores
            if (e.getMessage() != null && e.getMessage().contains("email")) {
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
     * OBTENER usuario por ID
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID",
            description = "Busca un usuario específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<ApiResponseDTO<Users>> getUserById(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer id) {

        Optional<Users> user = usersService.findById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Usuario encontrado",
                            HttpStatus.OK.value(),
                            user.get()
                    )
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseDTO.error(
                            "Usuario no encontrado",
                            HttpStatus.NOT_FOUND.value()
                    )
            );
        }
    }


}