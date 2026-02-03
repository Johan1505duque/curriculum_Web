package com.hse.Curriculum.Exception.Training;
/**
 * Excepción lanzada cuando no se encuentra un registro de capacitación
 */
public class TrainingNotFoundException extends RuntimeException {
    /**
     * Constructor con mensaje personalizado
     * @param message Mensaje descriptivo del error
     */
    public TrainingNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public TrainingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor con ID del registro no encontrado
     * @param trainingId ID del registro que no se encontró
     */
    public TrainingNotFoundException(Integer trainingId) {
        super("Registro de capacitación con ID " + trainingId + " no encontrado");
    }
}
