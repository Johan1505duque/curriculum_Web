package com.hse.Curriculum.Exception;

/**
 * Contraseña débil
 */
public class WeakPasswordException extends BusinessException {
    public WeakPasswordException() {
        super("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas y números");
    }
}