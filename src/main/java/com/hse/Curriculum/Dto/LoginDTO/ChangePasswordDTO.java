package com.hse.Curriculum.Dto.LoginDTO;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para cambio de contraseña
 */
@Schema(description = "Datos para cambiar la contraseña")
public class ChangePasswordDTO {

    @Schema(description = "Contraseña actual", example = "OldPassword123")
    private String currentPassword;

    @Schema(description = "Nueva contraseña", example = "NewPassword123")
    private String newPassword;

    public ChangePasswordDTO() {}

    public ChangePasswordDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
