package com.hse.Curriculum.Exception.Profile;

import com.hse.Curriculum.Exception.Login.BusinessException;

public class DuplicateDocumentException extends BusinessException {
    public DuplicateDocumentException(String documentNumber) {
        super("El número de documento " + documentNumber + " ya está registrado");
    }
}