package com.hse.Curriculum.Exception.Role;

/**
 * Excepción lanzada cuando se intenta crear un rol con un nombre que ya existe
 */
public class RoleDuplicateException extends RuntimeException {

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public RoleDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor con nombre del rol duplicado
     * @param roleName Nombre del rol que ya existe
     */
    public RoleDuplicateException(String roleName) {
        super("Ya existe un rol con el nombre '" + roleName + "'");
    }
}
