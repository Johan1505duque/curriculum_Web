package com.hse.Curriculum.Exception.Login;

/**
 * Contraseña actual incorrecta
 */
public class InvalidCurrentPasswordException extends BusinessException {
    public InvalidCurrentPasswordException(String message) {
        super(message);
    }
}
