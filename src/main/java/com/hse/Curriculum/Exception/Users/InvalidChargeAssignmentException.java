package com.hse.Curriculum.Exception.Users;

public class InvalidChargeAssignmentException extends RuntimeException {
    public InvalidChargeAssignmentException(Integer chargeId) {
        super("El cargo con ID " + chargeId + " no existe o está inactivo");
    }

    public InvalidChargeAssignmentException(String message) {
        super(message);
    }
}
