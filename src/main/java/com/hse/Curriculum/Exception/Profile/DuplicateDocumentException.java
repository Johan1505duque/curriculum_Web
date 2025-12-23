package com.hse.Curriculum.Exception.Profile;

public class DuplicateDocumentException extends RuntimeException {
    public DuplicateDocumentException(String documentNumber) {
        super("El número de documento " + documentNumber + " ya está registrado");
    }

}
