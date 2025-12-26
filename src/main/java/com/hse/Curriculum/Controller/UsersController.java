package com.hse.Curriculum.Controller;


import com.hse.Curriculum.Dto.ProfileDTO.ProfileResponseDTO;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("users")
@Tag(name = "Gestión de Usuarios", description = "Endpoints para crear y consultar usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // ⭐ Esto genera automáticamente el constructor con todos los "final"
public class UsersController {

    // ✅ CORRECTO: Con @RequiredArgsConstructor, estos se inyectan automáticamente
    private final UsersService usersService;
    private final AuditService auditService;
    private final ProfilesService profilesService; // ⭐ Nombre consistente

    // ❌ NO NECESITAS ESTE CONSTRUCTOR - Lombok lo genera automáticamente
    // Si quieres hacerlo manual, ELIMINA @RequiredArgsConstructor

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
    public ResponseEntity<?> register(
            @Valid @RequestBody UserSignUpDTO signUpDTO,
            HttpServletRequest request
    ) {
        try {
            // 1. Registrar usuario
            Users user = usersService.register(signUpDTO);

            // 2. Crear perfil vacío automáticamente
            // ✅ CORRECTO: profilesService con 's' y p minúscula (instancia, no estático)
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
                    "Registro de nuevo usuario con perfil inicial (ID: " + profile.getProfilesId() + ")",
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
                    "Usuario registrado exitosamente. Perfil creado con ID: " + profile.getProfilesId()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            // Manejar email duplicado u otros errores
            if (e.getMessage() != null && e.getMessage().contains("email")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "error", "Email ya registrado",
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
     * OBTENER usuario por ID
     * REQUIERE AUTENTICACIÓN
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID",
            description = "Busca un usuario específico por su ID")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
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
     * REQUIERE AUTENTICACIÓN
     */
    @PatchMapping("/{id}/disable")
    @Operation(summary = "Deshabilitar usuario",
            description = "Deshabilita un usuario del sistema sin eliminarlo permanentemente")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario deshabilitado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<?> disableUser(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer id,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedEmail = authentication.getName();
            Users authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Obtener usuario a deshabilitar
            Users userToDisable = usersService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar que solo pueda deshabilitar su propia cuenta
            // (O implementar lógica de roles/admin aquí)
            if (!authenticatedUser.getUserId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No autorizado para deshabilitar este usuario"));
            }

            // Deshabilitar
            usersService.disableUser(id);

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "users",
                    id,
                    AuditLog.AuditAction.DISABLE,
                    Map.of("isActive", true),
                    Map.of("isActive", false),
                    "Usuario deshabilitado",
                    request
            );

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
     * REQUIERE AUTENTICACIÓN
     */
    @PatchMapping("/{id}/enable")
    @Operation(summary = "Habilitar usuario",
            description = "Reactiva un usuario deshabilitado")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario habilitado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<?> enableUser(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer id,
            HttpServletRequest request) {
        try {
            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedEmail = authentication.getName();
            Users authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Verificar autorización (implementar lógica según tus necesidades)
            if (!authenticatedUser.getUserId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No autorizado para habilitar este usuario"));
            }

            // Habilitar
            usersService.enableUser(id);

            // Registrar en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "users",
                    id,
                    AuditLog.AuditAction.ENABLE,
                    Map.of("isActive", false),
                    Map.of("isActive", true),
                    "Usuario habilitado",
                    request
            );

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