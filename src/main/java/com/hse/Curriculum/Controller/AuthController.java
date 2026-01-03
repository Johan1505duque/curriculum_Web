package com.hse.Curriculum.Controller;

import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Service.AuditService;
import com.hse.Curriculum.Service.JwtService;
import com.hse.Curriculum.Service.LoginAuthService;
import com.hse.Curriculum.Service.UsersService;
import com.hse.Curriculum.Dto.LoginDTO.AuthResponseDTO;
import com.hse.Curriculum.Dto.LoginDTO.ChangePasswordDTO;
import com.hse.Curriculum.Dto.LoginDTO.LoginDTO;
import com.hse.Curriculum.Models.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para autenticación y operaciones relacionadas con contraseñas
 */
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthController {

    private final UsersService usersService;
    private final LoginAuthService loginAuthService;
    private final JwtService jwtService;
    private final AuditService auditService;

    /**
     * Login de usuario - Genera tokens JWT
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login de usuario",
            description = "Autentica un usuario con email y contraseña, retorna tokens JWT",
    security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa, tokens generados"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody LoginDTO loginDTO,
            HttpServletRequest request) {

        // Autenticar usuario
        Users user = loginAuthService.authenticate(
                loginDTO.getEmail(),
                loginDTO.getPassword()
        );

        String fullName = user.getFirstName() + " " + user.getLastName();

        // Generar tokens JWT
        String accessToken = jwtService.generateToken(
                user.getUserId(),
                user.getEmail(),
                fullName
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getUserId(),
                user.getEmail()
        );

        // Registrar login en auditoría
        auditService.logSimpleAction(
                user.getUserId(),
                user.getEmail(),
                fullName,
                AuditLog.AuditAction.LOGIN,
                "Inicio de sesión exitoso",
                request
        );

        // Construir respuesta
        AuthResponseDTO response = AuthResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(fullName)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .message("Autenticación exitosa")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Refresh token - Genera nuevo access token
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Renovar token de acceso",
            description = "Genera un nuevo access token usando un refresh token válido"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    public ResponseEntity<?> refreshToken(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        try {
            String refreshToken = request.get("refreshToken");

            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Refresh token requerido"));
            }

            // Extraer información del refresh token
            String email = jwtService.extractUsername(refreshToken);

            // Verificar que el token sea válido
            if (!jwtService.isTokenValid(refreshToken, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token inválido o expirado"));
            }

            // Obtener usuario
            Users user = usersService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String fullName = user.getFirstName() + " " + user.getLastName();

            // Generar nuevo access token
            String newAccessToken = jwtService.generateToken(
                    user.getUserId(),
                    user.getEmail(),
                    fullName
            );

            AuthResponseDTO response = AuthResponseDTO.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .fullName(fullName)
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getExpirationTime())
                    .message("Token renovado exitosamente")
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Error al renovar token",
                            "message", e.getMessage()));
        }
    }

    /**
     * Cambiar contraseña - REQUIERE AUTENTICACIÓN
     */
    @PutMapping("/change-password/{userId}")
    @Operation(
            summary = "Cambiar contraseña",
            description = "Permite a un usuario autenticado cambiar su contraseña"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para cambiar esta contraseña"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<String> changePassword(
            @PathVariable Integer userId,
            @RequestBody ChangePasswordDTO changePasswordDTO,
            HttpServletRequest request) {

        // Obtener usuario autenticado del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();

        // Obtener usuario del token
        Users authenticatedUser = usersService.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        // Verificar que el usuario solo pueda cambiar su propia contraseña
        if (!authenticatedUser.getUserId().equals(userId)) {
            return ResponseEntity.status(403)
                    .body("No tienes permiso para cambiar la contraseña de otro usuario");
        }

        // Cambiar contraseña
        loginAuthService.changePassword(
                userId,
                changePasswordDTO.getCurrentPassword(),
                changePasswordDTO.getNewPassword()
        );

        // Registrar cambio de contraseña en auditoría
        auditService.logAction(
                authenticatedUser.getUserId(),
                authenticatedUser.getEmail(),
                authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                "users",
                userId,
                AuditLog.AuditAction.CHANGE_PASSWORD,
                null,
                null,
                "Cambio de contraseña",
                request
        );

        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }

    /**
     * Logout - Registra el cierre de sesión
     */
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesión",
            description = "Registra el cierre de sesión del usuario"
    )
    @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Users user = usersService.findByEmail(email).orElse(null);

            if (user != null) {
                // Registrar logout en auditoría
                auditService.logSimpleAction(
                        user.getUserId(),
                        user.getEmail(),
                        user.getFirstName() + " " + user.getLastName(),
                        AuditLog.AuditAction.LOGOUT,
                        "Cierre de sesión",
                        request
                );
            }
        }

        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }
}