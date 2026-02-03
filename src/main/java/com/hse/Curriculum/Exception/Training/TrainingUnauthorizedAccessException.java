package com.hse.Curriculum.Exception.Training;

/**
 * Excepción lanzada cuando un usuario intenta acceder a un registro
 * de capacitación que no le pertenece y no tiene permisos suficientes
 */
public class TrainingUnauthorizedAccessException extends RuntimeException {
    /**
     * Constructor con mensaje personalizado
     * @param message Mensaje descriptivo del error
     */
    public TrainingUnauthorizedAccessException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public TrainingUnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor por defecto
     */
    public TrainingUnauthorizedAccessException() {
        super("No tiene permisos para acceder a este registro de capacitación");
    }
}
