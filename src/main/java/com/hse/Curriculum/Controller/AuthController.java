package com.hse.Curriculum.Controller;

import com.hse.Curriculum.Exception.Users.UserNotFoundException;
import com.hse.Curriculum.Models.AuditLog;
import com.hse.Curriculum.Service.AuditService;
import com.hse.Curriculum.Service.JwtService;
import com.hse.Curriculum.Service.LoginAuthService;
import com.hse.Curriculum.Service.UsersService;
import com.hse.Curriculum.Dto.LoginDTO.AuthResponseDTO;
import com.hse.Curriculum.Dto.LoginDTO.ChangePasswordDTO;
import com.hse.Curriculum.Dto.LoginDTO.LoginDTO;
import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Dto.ApiResponseDTO;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthController {

    private final UsersService usersService;
    private final LoginAuthService loginAuthService;
    private final JwtService jwtService;
    private final AuditService auditService;
    private final UserDetailsService userDetailsService;

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
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(
            @RequestBody LoginDTO loginDTO,
            HttpServletRequest request) {

        try {
            // Autenticar usuario
            Users user = loginAuthService.authenticate(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
            );

            String fullName = user.getFirstName() + " " + user.getLastName();

            // 1. Cargar UserDetails (Spring Security)
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(user.getEmail());

            // 2. Claims personalizados
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", user.getUserId());
            extraClaims.put("fullName", fullName);

            // 3. Generar tokens JWT
            String accessToken = jwtService.generateToken(extraClaims, userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            String roleName = user.getRole() != null ? user.getRole().getName() : "USER";
            // Registrar login en auditoría
            auditService.logSimpleAction(
                    user.getUserId(),
                    user.getEmail(),
                    roleName,
                    fullName,
                    AuditLog.AuditAction.LOGIN,
                    "Inicio de sesión exitoso",
                    request
            );



            // Construir respuesta
            AuthResponseDTO authData = AuthResponseDTO.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .fullName(fullName)
                    .roleName(roleName)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getExpirationTime())
                    .message("Autenticación exitosa")
                    .build();

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Autenticación exitosa",
                            HttpStatus.OK.value(),
                            authData
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponseDTO.error(
                            e.getMessage(),
                            HttpStatus.UNAUTHORIZED.value()
                    )
            );
        }
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
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> refreshToken(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        try {
            String refreshToken = request.get("refreshToken");

            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponseDTO.error(
                                "Refresh token requerido",
                                HttpStatus.BAD_REQUEST.value()
                        )
                );
            }

            // Extraer información del refresh token
            String email = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = usersService.loadUserByEmail(email);
            // Verificar que el token sea válido
            if (!jwtService.isTokenValid(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        ApiResponseDTO.error(
                                "Refresh token inválido o expirado",
                                HttpStatus.UNAUTHORIZED.value()
                        )
                );
            }

            // Obtener usuario
            Users user = usersService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String fullName = user.getFirstName() + " " + user.getLastName();

             email = jwtService.extractUsername(refreshToken);

            userDetails =
                    userDetailsService.loadUserByUsername(email);

            if (!jwtService.isTokenValid(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        ApiResponseDTO.error(
                                "Refresh token inválido o expirado",
                                HttpStatus.UNAUTHORIZED.value()
                        )
                );
            }

            user = usersService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            fullName = user.getFirstName() + " " + user.getLastName();

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", user.getUserId());
            extraClaims.put("fullName", fullName);

            String newAccessToken =
                    jwtService.generateToken(extraClaims, userDetails);

            AuthResponseDTO authData = AuthResponseDTO.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .fullName(fullName)
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getExpirationTime())
                    .message("Token renovado exitosamente")
                    .build();

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Token renovado exitosamente",
                            HttpStatus.OK.value(),
                            authData
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponseDTO.error(
                            "Error : " + e.getMessage(),
                            HttpStatus.UNAUTHORIZED.value()
                    )
            );
        }
    }

    /**
     * Cambiar contraseña
     */
    @PutMapping("/change-password")
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
    public ResponseEntity<ApiResponseDTO<Void>> changePassword(
            @RequestBody ChangePasswordDTO changePasswordDTO,
            HttpServletRequest request) {

        try {
            // Obtener usuario autenticado del contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedEmail = authentication.getName();

            // Obtener usuario del token
            Users authenticatedUser = usersService.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Cambiar contraseña
            loginAuthService.changePassword(
                    authenticatedUser.getUserId(),
                    changePasswordDTO.getCurrentPassword(),
                    changePasswordDTO.getNewPassword()
            );

            // Registrar cambio de contraseña en auditoría
            auditService.logAction(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                    "users",
                    authenticatedUser.getUserId(),
                    AuditLog.AuditAction.CHANGE_PASSWORD,
                    null,
                    null,
                    "Cambio de contraseña",
                    request
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Contraseña actualizada exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.error(
                            "Error: " + e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }
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
    public ResponseEntity<ApiResponseDTO<Void>> logout(HttpServletRequest request) {

        try {
            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                Users user = usersService.findByEmail(email).orElse(null);
                String roleName = user.getRole() != null ? user.getRole().getName() : "USER";
                if (user != null) {
                    // Registrar logout en auditoría
                    auditService.logSimpleAction(
                            user.getUserId(),
                            user.getEmail(),
                            roleName,
                            user.getFirstName() + " " + user.getLastName(),
                            AuditLog.AuditAction.LOGOUT,
                            "Cierre de sesión",
                            request
                    );
                }
            }

            return ResponseEntity.ok(
                    ApiResponseDTO.success(
                            "Sesión cerrada exitosamente",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDTO.error(
                            "Error:" + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    )
            );
        }
    }
}