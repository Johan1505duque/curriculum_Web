package com.hse.Curriculum.Exception.Role;
/**
 * Excepción lanzada cuando se intenta eliminar un rol que está siendo usado por usuarios
 */
public class RoleInUseException extends RuntimeException {
    /**
     * Constructor con mensaje personalizado
     * @param message Mensaje descriptivo del error
     */
    public RoleInUseException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public RoleInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor con nombre del rol y cantidad de usuarios
     * @param roleName Nombre del rol que está en uso
     * @param userCount Cantidad de usuarios que tienen este rol
     */
    public RoleInUseException(String roleName, Long userCount) {
        super("No se puede eliminar el rol '" + roleName + "' porque está asignado a " + userCount + " usuario(s)");
    }
}
