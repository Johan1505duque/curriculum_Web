package com.hse.Curriculum.Service;

import com.hse.Curriculum.Exception.Login.WeakPasswordException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servicio para validar la fortaleza de las contraseñas
 */
@Service
public class PasswordValidator {

    // Expresiones regulares para validación
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

    private static final int MIN_LENGTH = 8;

    /**
     * Valida que la contraseña cumpla con todos los requisitos de seguridad
     *
     * @param password Contraseña a validar
     * @param firstName Nombre del usuario (no debe estar en la contraseña)
     * @param lastName Apellido del usuario (no debe estar en la contraseña)
     * @throws WeakPasswordException si la contraseña no cumple los requisitos
     */
    public void validatePassword(String password, String firstName, String lastName) {
        if (password == null || password.trim().isEmpty()) {
            throw new WeakPasswordException("La contraseña no puede estar vacía");
        }

        List<String> errors = new ArrayList<>();

        // 1. Validar longitud mínima
        if (password.length() < MIN_LENGTH) {
            errors.add("Debe tener al menos " + MIN_LENGTH + " caracteres");
        }

        // 2. Validar mayúscula
        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            errors.add("Debe contener al menos una letra mayúscula (A-Z)");
        }

        // 3. Validar minúscula
        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            errors.add("Debe contener al menos una letra minúscula (a-z)");
        }

        // 4. Validar número
        if (!DIGIT_PATTERN.matcher(password).matches()) {
            errors.add("Debe contener al menos un número (0-9)");
        }

        // 5. Validar carácter especial
        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            errors.add("Debe contener al menos un carácter especial (!@#$%^&*()_+-=[]{}...)");
        }

        // 6. Validar que no contenga el nombre (case insensitive)
        if (firstName != null && !firstName.trim().isEmpty()) {
            if (password.toLowerCase().contains(firstName.toLowerCase())) {
                errors.add("No puede contener tu nombre");
            }
        }

        // 7. Validar que no contenga el apellido (case insensitive)
        if (lastName != null && !lastName.trim().isEmpty()) {
            if (password.toLowerCase().contains(lastName.toLowerCase())) {
                errors.add("No puede contener tu apellido");
            }
        }

        // Si hay errores, lanzar excepción con todos los mensajes
        if (!errors.isEmpty()) {
            String errorMessage = "La contraseña no cumple con los requisitos de seguridad:\n- "
                    + String.join("\n- ", errors);
            throw new WeakPasswordException(errorMessage);
        }
    }

    /**
     * Verifica si una contraseña es válida (retorna true/false en lugar de lanzar excepción)
     */
    public boolean isPasswordValid(String password, String firstName, String lastName) {
        try {
            validatePassword(password, firstName, lastName);
            return true;
        } catch (WeakPasswordException e) {
            return false;
        }
    }

    /**
     * Genera mensaje de ayuda con los requisitos de contraseña
     */
    public String getPasswordRequirements() {
        return "La contraseña debe cumplir los siguientes requisitos:\n" +
                "- Mínimo " + MIN_LENGTH + " caracteres\n" +
                "- Al menos una letra mayúscula (A-Z)\n" +
                "- Al menos una letra minúscula (a-z)\n" +
                "- Al menos un número (0-9)\n" +
                "- Al menos un carácter especial (!@#$%^&*...)\n" +
                "- No puede contener tu nombre o apellido";
    }
}