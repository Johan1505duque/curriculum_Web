package com.hse.Curriculum.Exception.Login;

/**
 * Email duplicado
 */
public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String email) {
        super("El email ya está registrado: " + email);
    }
}
