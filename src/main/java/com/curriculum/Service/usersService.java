package com.curriculum.Service;

import com.curriculum.models.users;
import com.curriculum.repository.usersRepository;
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
        return usersRepository.findByEmail(email);
    }

    /**
     * Guardar nuevo usuario
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
     * Actualizar usuario
     */
    @Transactional
    public users update(users user) {
        return usersRepository.save(user);
    }

    /**
     * Eliminar usuario
     */
    @Transactional
    public void deleteById(Integer userId) {
        usersRepository.deleteById(userId);
    }
}