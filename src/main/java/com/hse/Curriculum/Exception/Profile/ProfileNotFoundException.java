package com.hse.Curriculum.Exception.Profile;

import com.hse.Curriculum.Exception.Login.BusinessException;

public class ProfileNotFoundException extends BusinessException {
    public ProfileNotFoundException(Integer userId) {
        super("Perfil no encontrado para el usuario con ID: " + userId);
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }
}