package com.hse.Curriculum.Exception.Country;

/**
 * Excepción cuando no se encuentra un país
 */
public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String message) {
        super(message);
    }
}
