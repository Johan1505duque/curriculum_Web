package com.hse.Curriculum.Models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
public class Profiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profiles_id")
    private Integer profilesId;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users user;

    // ========== PERFIL ==========
    @Column(name = "document_type", length = 100)
    private String documentType;

    @Column(name = "document_number", length = 100)
    private String documentNumber;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Column(name = "residential_address")
    private String residentialAddress;

    // ========== COMPLEMENTO ==========
    @Column(name = "professional_summary", columnDefinition = "TEXT")
    private String professionalSummary;

    @Column(name = "career_achievements", columnDefinition = "TEXT")
    private String careerAchievements;

    // ========== FECHAS ==========
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "profile_complete", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean profileComplete = false;

    // ========== CONSTRUCTORES ==========
    public Profiles() {}

    // Constructor para COMPLEMENTO PERFIL
    public Profiles(String professionalSummary, String careerAchievements,
                     Users user) {
        this.professionalSummary = professionalSummary;
        this.careerAchievements = careerAchievements;
        this.user = user;
    }

    // Constructor para REGISTRO PERFIL
    public Profiles(String documentType, String documentNumber, String residentialAddress,
                    String phoneNumber, LocalDate birthDate, Users user) {
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.residentialAddress = residentialAddress;
        this.user = user;
    }

    // ========== LIFECYCLE CALLBACKS ==========
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @Transient
    public boolean hasMinimumData() {
        return documentNumber != null && !documentNumber.isEmpty() &&
                phoneNumber != null && !phoneNumber.isEmpty() &&
                birthDate != null;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ========== GETTERS Y SETTERS ==========

    public Integer getProfilesId() {
        return profilesId;
    }

    public void setProfilesId(Integer profilesId) {
        this.profilesId = profilesId;
    }

    public Integer getUserId() {
        return user != null ? user.getUserId() : null;
    }

    public void setUserId(Integer userId) {
        if (this.user == null) {
            this.user = new Users();
        }
        this.user.setUserId(userId);
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String setProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public String getCareerAchievements() {
        return careerAchievements;
    }

    public void setCareerAchievements(String careerAchievements) {
        this.careerAchievements = careerAchievements;
    }

    public Boolean getProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(Boolean profileComplete) {
        this.profileComplete = profileComplete;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}