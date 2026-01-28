package com.hse.Curriculum.Exception.Country;

/**
 * Excepción cuando ya existe un país con los mismos datos
 */
public class CountryAlreadyExistsException extends RuntimeException {
    public CountryAlreadyExistsException(String message) {
        super(message);
    }
}
