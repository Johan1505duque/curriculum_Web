package com.hse.Curriculum.Exception.Post;

public class ChargeNotFoundException extends RuntimeException {
    public ChargeNotFoundException(Integer postId) {
        super("Cargo con ID " + postId + " no encontrado");
    }
    public ChargeNotFoundException(String message) {
        super(message);
    }

}
