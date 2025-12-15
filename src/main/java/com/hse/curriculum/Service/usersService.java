package com.hse.curriculum.Service;

import com.hse.curriculum.repository.usersRepository;
import com.hse.curriculum.models.users;
import com.hse.curriculum.Security.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class usersService {

    @Autowired
    private usersRepository usersRepository;

    @Autowired
    private PasswordService passwordService; // ⭐ NUEVA INYECCIÓN

    /**
     * Buscar usuario por ID
     */
    public Optional<users> findById(Integer userId) {
        System.out.println("🔍 Buscando usuario con ID: " + userId);
        Optional<users> user = usersRepository.findById(userId);

        if (user.isPresent()) {
            System.out.println("✅ Usuario encontrado: " + user.get().getEmail());
        } else {
            System.out.println("⚠️  Usuario con ID " + userId + " no existe");
        }

        return user;
    }

    /**
     * Buscar usuario por email
     */
    public Optional<users> findByEmail(String email) {
        System.out.println("🔍 Buscando usuario con email: " + email);
        Optional<users> user = usersRepository.findByEmail(email);

        if (user.isPresent()) {
            System.out.println("✅ Usuario encontrado: " + user.get().getEmail());
        } else {
            System.out.println("⚠️  Usuario con email " + email + " no existe");
        }

        return user;
    }

    /**
     * Guardar nuevo usuario (con nombre y apellido separados)
     */
    @Transactional
    public users save(String firstName, String lastName, String email, String password) {
        System.out.println("🔄 Intentando guardar usuario: " + email);

        // Verificar si el email ya existe
        if (usersRepository.existsByEmail(email)) {
            System.out.println("❌ El email " + email + " ya está registrado");
            throw new RuntimeException("El email ya está registrado");
        }

        // ⭐ VALIDAR FORTALEZA DE CONTRASEÑA
        if (!passwordService.isPasswordStrong(password)) {
            System.out.println("❌ La contraseña no cumple los requisitos de seguridad");
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas y números");
        }

        // ⭐ HASHEAR LA CONTRASEÑA ANTES DE GUARDAR
        String hashedPassword = passwordService.hashPassword(password);

        users newUser = new users(firstName, lastName, email, hashedPassword);
        users savedUser = usersRepository.save(newUser);

        System.out.println("✅ Usuario guardado exitosamente!");
        System.out.println("   ID: " + savedUser.getUserId());
        System.out.println("   Nombre: " + savedUser.getFirstName() + " " + savedUser.getLastName());
        System.out.println("   Email: " + savedUser.getEmail());
        System.out.println("   🔒 Contraseña hasheada correctamente");

        return savedUser;
    }

    /**
     * Guardar nuevo usuario con NOMBRE COMPLETO
     */
    @Transactional
    public users saveWithFullName(String fullName, String email, String password) {
        System.out.println("🔄 Intentando guardar usuario con nombre completo: " + fullName);

        if (usersRepository.existsByEmail(email)) {
            System.out.println("❌ El email " + email + " ya está registrado");
            throw new RuntimeException("El email ya está registrado");
        }

        // ⭐ VALIDAR FORTALEZA DE CONTRASEÑA
        if (!passwordService.isPasswordStrong(password)) {
            System.out.println("❌ La contraseña no cumple los requisitos de seguridad");
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas y números");
        }

        String[] nameParts = splitFullName(fullName);
        String firstName = nameParts[0];
        String lastName = nameParts[1];

        System.out.println("📝 Nombre separado:");
        System.out.println("   Nombre: " + firstName);
        System.out.println("   Apellido: " + lastName);

        // ⭐ HASHEAR LA CONTRASEÑA
        String hashedPassword = passwordService.hashPassword(password);

        users newUser = new users(firstName, lastName, email, hashedPassword);
        users savedUser = usersRepository.save(newUser);

        System.out.println("✅ Usuario guardado exitosamente!");
        System.out.println("   ID: " + savedUser.getUserId());
        System.out.println("   Nombre completo: " + savedUser.getFirstName() + " " + savedUser.getLastName());
        System.out.println("   Email: " + savedUser.getEmail());
        System.out.println("   🔒 Contraseña hasheada correctamente");

        return savedUser;
    }

    /**
     * ⭐ NUEVO: Autenticar usuario (login)
     */
    public boolean authenticate(String email, String password) {
        System.out.println("🔐 Intentando autenticar usuario: " + email);

        Optional<users> userOpt = usersRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            System.out.println("❌ Usuario no encontrado");
            return false;
        }

        users user = userOpt.get();
        boolean isValid = passwordService.verifyPassword(password, user.getPassword());

        if (isValid) {
            System.out.println("✅ Autenticación exitosa para: " + email);
        } else {
            System.out.println("❌ Contraseña incorrecta para: " + email);
        }

        return isValid;
    }

    /**
     * ⭐ NUEVO: Cambiar contraseña
     */
    @Transactional
    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        System.out.println("🔄 Cambiando contraseña para usuario ID: " + userId);

        users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordService.verifyPassword(currentPassword, user.getPassword())) {
            System.out.println("❌ Contraseña actual incorrecta");
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // Validar nueva contraseña
        if (!passwordService.isPasswordStrong(newPassword)) {
            System.out.println("❌ La nueva contraseña no cumple los requisitos");
            throw new RuntimeException("La nueva contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas y números");
        }

        // Actualizar contraseña
        String hashedPassword = passwordService.hashPassword(newPassword);
        user.setPassword(hashedPassword);
        usersRepository.save(user);

        System.out.println("✅ Contraseña actualizada exitosamente");
    }

    private String[] splitFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo no puede estar vacío");
        }

        fullName = fullName.trim();
        int spaceIndex = fullName.indexOf(' ');

        if (spaceIndex > 0) {
            String firstName = fullName.substring(0, spaceIndex).trim();
            String lastName = fullName.substring(spaceIndex + 1).trim();
            return new String[]{firstName, lastName};
        } else {
            return new String[]{fullName, ""};
        }
    }

    @Transactional
    public users update(users user) {
        System.out.println("🔄 Actualizando usuario ID: " + user.getUserId());
        return usersRepository.save(user);
    }

    @Transactional
    public void deleteById(Integer userId) {
        System.out.println("🗑️  Eliminando usuario ID: " + userId);
        usersRepository.deleteById(userId);
    }

    public boolean existsById(Integer userId) {
        return usersRepository.existsById(userId);
    }

    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }
}