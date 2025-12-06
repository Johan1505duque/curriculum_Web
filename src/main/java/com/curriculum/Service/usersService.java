package com.curriculum.Service;

import com.curriculum.repository.usersRepository;
import com.curriculum.models.users;

public class usersService {

    private usersRepository usersRepository;

    // Constructor
    public usersService() {
        this.usersRepository = new usersRepository();
    }

    /**
     * Busca un usuario por su ID
     * @param userId ID del usuario a buscar
     * @return Usuario encontrado o null si no existe
     */
    public users getUserById(int userId) {
        if (userId <= 0) {
            System.out.println("ID de usuario inválido");
            return null;
        }

        users user = usersRepository.findById(userId);

        if (user == null) {
            System.out.println("Usuario no encontrado con ID: " + userId);
        }

        return user;
    }

    /**
     * Registra un nuevo usuario
     * @param firstName Nombre del usuario
     * @param lastName Apellido del usuario
     * @param email Correo electrónico
     * @param password Contraseña
     * @return Usuario creado o null si hay error
     */
    public users registerUser(String firstName, String lastName, String email, String password) {
        // Validaciones de negocio
        if (!validateUserData(firstName, lastName, email, password)) {
            return null;
        }

        // Guarda el usuario en la base de datos
        users newUser = usersRepository.save(firstName, lastName, email, password);

        if (newUser != null) {
            System.out.println("Usuario registrado exitosamente: " + newUser.getEmail());
        } else {
            System.out.println("Error al registrar el usuario");
        }

        return newUser;
    }

    /**
     * Valida los datos del usuario antes de guardar
     */
    private boolean validateUserData(String first_Name, String last_Name, String email, String password) {
        if (first_Name == null || first_Name.trim().isEmpty()) {
            System.out.println("El nombre es obligatorio");
            return false;
        }

        if (last_Name == null || last_Name.trim().isEmpty()) {
            System.out.println("El apellido es obligatorio");
            return false;
        }

        if (email == null || !isValidEmail(email)) {
            System.out.println("Email inválido");
            return false;
        }

        if (password == null || password.length() < 6) {
            System.out.println("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        return true;
    }

    /**
     * Valida formato de email
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Verifica si un usuario existe por su ID
     */
    public boolean userExists(int userId) {
        return getUserById(userId) != null;
    }

    /**
     * Actualiza la información de un usuario
     * (necesitarías crear el método update en el Repository)
     */
    public users updateUser(int userId, String firstName, String lastName, String email) {
        if (!userExists(userId)) {
            System.out.println("El usuario no existe");
            return null;
        }

        if (!validateBasicData(firstName, lastName, email)) {
            return null;
        }

        // Aquí llamarías al método update del repository
        // return usersRepository.update(userId, firstName, lastName, email);

        System.out.println("Método de actualización pendiente de implementar en Repository");
        return null;
    }

    /**
     * Validación básica sin password
     */
    private boolean validateBasicData(String firstName, String lastName, String email) {
        if (firstName == null || firstName.trim().isEmpty()) {
            System.out.println("El nombre es obligatorio");
            return false;
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            System.out.println("El apellido es obligatorio");
            return false;
        }

        if (email == null || !isValidEmail(email)) {
            System.out.println("Email inválido");
            return false;
        }

        return true;
    }
}
