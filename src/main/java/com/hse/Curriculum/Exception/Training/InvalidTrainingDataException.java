package com.hse.Curriculum.Exception.Training;

/**
 * Excepción lanzada cuando los datos de una capacitación son inválidos
 */
public class InvalidTrainingDataException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado
     * @param message Mensaje descriptivo del error
     */
    public InvalidTrainingDataException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public InvalidTrainingDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
