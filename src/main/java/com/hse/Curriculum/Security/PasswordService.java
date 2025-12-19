package com.hse.Curriculum.Security;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return PasswordHasher.hash(plainPassword);
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return PasswordHasher.verify(plainPassword, hashedPassword);
    }

    public boolean isPasswordStrong(String password) {
        if (password == null) {
            return false;
        }

        // Requisitos: mínimo 6 caracteres, mayúscula, minúscula, número
        return password.length() >= 6
                && password.matches(".*[A-Z].*")  // Al menos una mayúscula
                && password.matches(".*[a-z].*")  // Al menos una minúscula
                && password.matches(".*\\d.*");   // Al menos un número
    }
}