package com.hse.Curriculum.Exception.Profile;

public class ProfileAlreadyExistsException extends RuntimeException {
    public ProfileAlreadyExistsException(String message) {
        super(message);
    }

    public ProfileAlreadyExistsException(Integer userId) {
        super("El usuario con ID " + userId + " ya tiene un perfil creado");
    }
}
