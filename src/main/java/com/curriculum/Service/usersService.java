package com.curriculum.Service;

import com.curriculum.repository.usersRepository;
import com.curriculum.models.users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class usersService {

    @Autowired
    private usersRepository usersRepository;

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

        users newUser = new users(firstName, lastName, email, password);
        users savedUser = usersRepository.save(newUser);

        System.out.println("✅ Usuario guardado exitosamente!");
        System.out.println("   ID: " + savedUser.getUserId());
        System.out.println("   Nombre: " + savedUser.getFirstName() + " " + savedUser.getLastName());
        System.out.println("   Email: " + savedUser.getEmail());

        return savedUser;
    }

    /**
     * Guardar nuevo usuario con NOMBRE COMPLETO (lo separa automáticamente)
     */
    @Transactional
    public users saveWithFullName(String fullName, String email, String password) {
        System.out.println("🔄 Intentando guardar usuario con nombre completo: " + fullName);

        // Verificar si el email ya existe
        if (usersRepository.existsByEmail(email)) {
            System.out.println("❌ El email " + email + " ya está registrado");
            throw new RuntimeException("El email ya está registrado");
        }

        // Separar nombre completo
        String[] nameParts = splitFullName(fullName);
        String firstName = nameParts[0];
        String lastName = nameParts[1];

        System.out.println("📝 Nombre separado:");
        System.out.println("   Nombre: " + firstName);
        System.out.println("   Apellido: " + lastName);

        // Crear y guardar usuario
        users newUser = new users(firstName, lastName, email, password);
        users savedUser = usersRepository.save(newUser);

        System.out.println("✅ Usuario guardado exitosamente!");
        System.out.println("   ID: " + savedUser.getUserId());
        System.out.println("   Nombre completo: " + savedUser.getFirstName() + " " + savedUser.getLastName());
        System.out.println("   Email: " + savedUser.getEmail());

        return savedUser;
    }

    private String[] splitFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo no puede estar vacío");
        }

        fullName = fullName.trim();

        // Buscar el primer espacio
        int spaceIndex = fullName.indexOf(' ');

        if (spaceIndex > 0) {
            // Hay al menos un espacio - separar en nombre y apellido
            String firstName = fullName.substring(0, spaceIndex).trim();
            String lastName = fullName.substring(spaceIndex + 1).trim();
            return new String[]{firstName, lastName};
        } else {
            // No hay espacios - usar todo como nombre
            return new String[]{fullName, ""};
        }
    }

    /**
     * Actualizar usuario
     */
    @Transactional
    public users update(users user) {
        System.out.println("🔄 Actualizando usuario ID: " + user.getUserId());
        return usersRepository.save(user);
    }

    /**
     * Eliminar usuario
     */
    @Transactional
    public void deleteById(Integer userId) {
        System.out.println("🗑️  Eliminando usuario ID: " + userId);
        usersRepository.deleteById(userId);
    }

    /**
     * Verificar si un usuario existe por ID
     */
    public boolean existsById(Integer userId) {
        return usersRepository.existsById(userId);
    }

    /**
     * Verificar si un email ya está registrado
     */
    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }
}