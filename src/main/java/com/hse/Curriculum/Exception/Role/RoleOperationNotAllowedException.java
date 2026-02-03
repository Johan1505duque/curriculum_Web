package com.hse.Curriculum.Exception.Role;

public class RoleOperationNotAllowedException extends RuntimeException {
    /**
     * Constructor con mensaje personalizado
     * @param message Mensaje descriptivo del error
     */
    public RoleOperationNotAllowedException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public RoleOperationNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor por defecto para roles del sistema
     */
    public RoleOperationNotAllowedException() {
        super("No se pueden modificar o eliminar roles del sistema");
    }
}
