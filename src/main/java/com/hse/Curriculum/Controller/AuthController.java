package com.hse.Curriculum.Controller;

import com.hse.Curriculum.Service.LoginAuthService;
import com.hse.Curriculum.Service.UsersService;
import com.hse.Curriculum.Dto.LoginDTO.AuthResponseDTO;
import com.hse.Curriculum.Dto.LoginDTO.ChangePasswordDTO;
import com.hse.Curriculum.Dto.LoginDTO.LoginDTO;
import com.hse.Curriculum.Models.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para autenticación y operaciones relacionadas con contraseñas
 */
@RestController
@RequestMapping("auth")
@Tag(name = "Autenticación", description = "Endpoints para login y gestión de contraseñas")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsersService usersService;
    private final LoginAuthService loginAuthService;

    public AuthController(LoginAuthService loginAuthService) {
        this.loginAuthService = loginAuthService;
    }
    /**
     * Login de usuario
     */
    @PostMapping("/login")
    @Operation(summary = "Login de usuario", description = "Autentica un usuario con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {

        Users user = loginAuthService.authenticate(
                loginDTO.getEmail(),
                loginDTO.getPassword()
        );

        AuthResponseDTO response = new AuthResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                "Autenticación exitosa"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Cambiar contraseña
     */
    @PutMapping("/change-password/{userId}")
    @Operation(summary = "Cambiar contraseña", description = "Permite a un usuario cambiar su contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta o nueva contraseña débil"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<String> changePassword(
            @PathVariable Integer userId,
            @RequestBody ChangePasswordDTO changePasswordDTO) {

        loginAuthService.changePassword(
                userId,
                changePasswordDTO.getCurrentPassword(),
                changePasswordDTO.getNewPassword()
        );

        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }
}