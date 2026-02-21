package com.hse.Curriculum.Dto.LoginDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para respuesta de autenticación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación exitosa")
public class AuthResponseDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Integer userId;

    @Schema(description = "Email del usuario", example = "juan@email.com")
    private String email;

    @Schema(description = "Rol del usuario", example = "ADMIN")
    private String roleName;

    @Schema(description = "Cargo del usuario", example = "Auxiliar de Facturacion")
    private String chargeName;

    @Schema(description = "Nombre completo", example = "Juan Pérez")
    private String fullName;

    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;

    @Schema(description = "Mensaje de éxito", example = "Autenticación exitosa")
    private String message;



    public AuthResponseDTO(Integer userId,
                           String email,
                           String fullName,
                           String message,
                           String roleName,
                           String chargeName) {
        this.userId = userId;
        this.email = email;
        this.roleName = roleName;
        this.fullName = fullName;
        this.message = message;
        this.chargeName = chargeName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public String getRoleName(){return roleName;}

    public void setRoleName(String roleName){this.roleName = roleName;}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

