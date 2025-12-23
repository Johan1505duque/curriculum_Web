package com.hse.Curriculum.Exception.Login;

/**
 * Credenciales inválidas
 */
public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
