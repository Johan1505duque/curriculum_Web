package com.hse.Curriculum.Exception;

/**
 * Credenciales inválidas
 */
public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
