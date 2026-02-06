package com.hse.Curriculum.Exception.Post;

public class DuplicateChargeNameException extends RuntimeException {
    public DuplicateChargeNameException(String namePost) {
        super("Ya existe un cargo con el nombre: " + namePost);
    }
}
