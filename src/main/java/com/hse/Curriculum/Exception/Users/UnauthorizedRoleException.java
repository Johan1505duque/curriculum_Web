package com.hse.Curriculum.Exception.Users;

/**
 * Excepción lanzada cuando un usuario intenta realizar una acción
 * para la cual no tiene permisos según su rol
 */
public class UnauthorizedRoleException extends RuntimeException {
    public UnauthorizedRoleException() {
        super("No tienes permisos para realizar esta acción");
    }

    public UnauthorizedRoleException(String message) {
        super(message);
    }

    public UnauthorizedRoleException(String requiredRole, String userRole) {
        super(String.format(
                "Acción requiere rol '%s' pero el usuario tiene rol '%s'",
                requiredRole,
                userRole
        ));
    }
}
