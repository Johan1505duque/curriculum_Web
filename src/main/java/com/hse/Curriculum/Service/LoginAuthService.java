package com.hse.Curriculum.Service;

import com.hse.Curriculum.Models.Users;
import com.hse.Curriculum.Security.PasswordService;
import com.hse.Curriculum.Exception.Login.InvalidCredentialsException;
import com.hse.Curriculum.Repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginAuthService {

    private final UsersRepository usersRepository;
    private final PasswordService passwordService;

    public LoginAuthService(UsersRepository usersRepository,
                            PasswordService passwordService) {
        this.usersRepository = usersRepository;
        this.passwordService = passwordService;
    }

    /**
     * Registro de nuevo usuario
     */
    @Transactional
    public Users register(String firstName, String lastName, String email, String password) {
        System.out.println("🔄 Registrando usuario: " + email);

        if (usersRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (!passwordService.isPasswordStrong(password)) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas y números");
        }

        String hashedPassword = passwordService.hashPassword(password);

        Users newUser = new Users(firstName, lastName, email, hashedPassword);
        Users savedUser = usersRepository.save(newUser);

        System.out.println("✅ Usuario registrado exitosamente!");
        return savedUser;
    }

    /**
     * Autenticación (login)
     */
    public Users authenticate(String email, String password) {
        System.out.println("🔐 Intentando autenticar: " + email);

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        if (!passwordService.verifyPassword(password, user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        System.out.println("✅ Autenticación exitosa!");
        return user;
    }

    /**
     * Cambiar contraseña
     */
    @Transactional
    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordService.verifyPassword(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("La contraseña actual es incorrecta");
        }

        if (!passwordService.isPasswordStrong(newPassword)) {
            throw new RuntimeException("La nueva contraseña no cumple los requisitos");
        }

        user.setPassword(passwordService.hashPassword(newPassword));
        usersRepository.save(user);
    }
}
