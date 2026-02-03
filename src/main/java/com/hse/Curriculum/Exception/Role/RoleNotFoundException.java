package com.hse.Curriculum.Exception.Role;
/**
 * Excepción lanzada cuando no se encuentra un rol
 */
public class RoleNotFoundException extends RuntimeException {
    /**
     * Constructor con mensaje personalizado
     * @param message Mensaje descriptivo del error
     */
    public RoleNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor con ID del rol no encontrado
     * @param roleId ID del rol que no se encontró
     */
    public RoleNotFoundException(Integer roleId) {
        super("Rol con ID " + roleId + " no encontrado");
    }

    /**
     * Constructor con nombre del rol no encontrado
     * @param roleName Nombre del rol que no se encontró
     */
    public RoleNotFoundException(String roleName, boolean byName) {
        super("Rol con nombre '" + roleName + "' no encontrado");
    }
}
