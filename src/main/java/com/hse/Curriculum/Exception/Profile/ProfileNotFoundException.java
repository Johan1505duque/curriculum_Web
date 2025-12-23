package com.hse.Curriculum.Exception.Profile;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(Integer userId) {
        super("Perfil no encontrado para el usuario con ID: " + userId);
    }
}
