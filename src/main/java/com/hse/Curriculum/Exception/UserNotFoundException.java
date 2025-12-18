package com.hse.Curriculum.Exception;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Integer userId) {
        super("Usuario no encontrado con ID: " + userId);
    }

    public UserNotFoundException(String email) {
        super("Usuario no encontrado con email: " + email);
    }

}
