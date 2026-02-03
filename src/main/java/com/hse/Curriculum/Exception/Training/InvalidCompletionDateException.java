package com.hse.Curriculum.Exception.Training;
/**
 * Excepción lanzada cuando la fecha de finalización de una capacitación es inválida
 * Por ejemplo, si el curso está marcado como completado pero no tiene fecha de finalización
 */
public class InvalidCompletionDateException extends RuntimeException {
    /**
     * Constructor con mensaje personalizado
     * @param message Mensaje descriptivo del error
     */
    public InvalidCompletionDateException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public InvalidCompletionDateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor por defecto para cursos completados sin fecha
     */
    public InvalidCompletionDateException() {
        super("Los cursos marcados como completados deben tener una fecha de finalización");
    }
}
