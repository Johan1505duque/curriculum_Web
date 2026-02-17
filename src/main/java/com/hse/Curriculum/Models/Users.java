package com.hse.Curriculum.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "charge_id", referencedColumnName = "charge_id")
    private Charge  charge;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Profiles profile;

    // ========== DATOS BÁSICOS ==========
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    // ========== AUTENTICACIÓN ==========
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    // ========== CONTROL ==========
    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ========== CONSTRUCTORES ==========
    public Users() {}

    // Constructor para REGISTRO inicial sin Roll
    public Users(String firstName,
                 String lastName,
                 String email,
                 String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.status = true;
    }

    // Constructor para REGISTRO inicial con Roll
    public Users(String firstName,
                 String lastName,
                 String email,
                 String password,
                 Roles role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = true;
    }


    // ========== GETTERS Y SETTERS ==========

   public Integer getRoleId() {
        return role != null ? role.getRoleId() : null;
    }
    //Validar el roll del usuario
    public boolean hasRole(String roleName) {
        return role != null &&
                role.getName() != null &&
                role.getName().equalsIgnoreCase(roleName);
    }

    //validar si el usuario tiene roll Admin
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    //validar si el usuario tiene roll User
    public boolean isUser() {
        return hasRole("USER");
    }
}