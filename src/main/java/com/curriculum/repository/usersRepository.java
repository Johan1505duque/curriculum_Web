package com.curriculum.repository;

import com.curriculum.database.connectionFactory;
import com.curriculum.models.users;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class usersRepository {

    /**
     * Buscar usuario por ID
     */
    public users findById(int user_Id) {
        // SIN comillas - PostgreSQL las convierte a minúsculas automáticamente
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user_Id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                users user = new users();
                // Al leer, PostgreSQL devuelve todo en minúsculas
                user.setUser_id(rs.getInt("user_id"));
                user.setFirst_Name(rs.getString("first_name"));
                user.setLast_Name(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setStatus(rs.getBoolean("status"));

                if (rs.getTimestamp("created_at") != null) {
                    user.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
                }

                System.out.println("✅ Usuario encontrado: " + user.getEmail());
                return user;
            } else {
                System.out.println("⚠️  Usuario con ID " + user_Id + " no existe");
            }

        } catch (Exception e) {
            System.out.println("❌ Error al consultar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Guardar nuevo usuario
     */
    public users save(String firstName, String lastName, String email, String password) {
        // TODO en minúsculas, SIN comillas
        String sql = "INSERT INTO users (first_name, last_name, email, password, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW()) RETURNING *";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("🔄 Intentando guardar usuario: " + email);

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setBoolean(5, true);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                users user = new users();
                // Leer en minúsculas
                user.setUser_id(rs.getInt("user_id"));
                user.setFirst_Name(rs.getString("first_name"));
                user.setLast_Name(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setStatus(rs.getBoolean("status"));

                if (rs.getTimestamp("created_at") != null) {
                    user.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
                }

                System.out.println("✅ Usuario guardado exitosamente!");
                System.out.println("   ID: " + user.getUser_id());
                System.out.println("   Nombre: " + user.getFirst_Name() + " " + user.getLast_Name());
                System.out.println("   Email: " + user.getEmail());

                return user;
            }

        } catch (Exception e) {
            System.out.println("❌ ERROR al insertar usuario:");
            System.out.println("   Email intentado: " + email);
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}