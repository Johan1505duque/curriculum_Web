package com.hse.Curriculum.Exception.Country;

/**
 * Excepción cuando se intenta eliminar un país que tiene relaciones
 */
public class CountryInUseException extends RuntimeException {
    public CountryInUseException(String message) {
        super(message);
    }
}
