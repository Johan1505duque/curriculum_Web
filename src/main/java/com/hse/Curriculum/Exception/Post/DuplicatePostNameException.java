package com.hse.Curriculum.Exception.Post;

public class DuplicatePostNameException extends RuntimeException {
    public DuplicatePostNameException(String namePost) {
        super("Ya existe un cargo con el nombre: " + namePost);
    }
}
