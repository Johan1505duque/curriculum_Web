package com.hse.Curriculum.Exception.Post;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Integer postId) {
        super("Cargo con ID " + postId + " no encontrado");
    }
    public PostNotFoundException(String message) {
        super(message);
    }

}
