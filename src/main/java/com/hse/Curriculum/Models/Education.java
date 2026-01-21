package com.hse.Curriculum.Models;

import com.hse.Curriculum.Enum.EducationTypeEnum;
import com.hse.Curriculum.Enum.GraduateStatusEnum;
import com.hse.Curriculum.Converter.EducationTypeConverter;
import com.hse.Curriculum.Converter.GraduateStatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "education")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Integer educationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code")
    private Country country;

    @Column(name = "type_education", nullable = false, columnDefinition = "education_type")
    @Convert(converter = EducationTypeConverter.class)
    private EducationTypeEnum typeEducation;

    @Column(name = "academic_level", nullable = false, length = 100)
    private String academicLevel;

    @Column(name = "institution_name", nullable = false, length = 255)
    private String institutionName;

    @Column(name = "degree_title", length = 255)
    private String degreeTitle;

    @Column(name = "graduate_status", columnDefinition = "graduate_status_enum DEFAULT 'no'")
    @Convert(converter = GraduateStatusConverter.class)
    private GraduateStatusEnum graduateStatus = GraduateStatusEnum.NO;

    @Column(name = "last_level_completed", length = 100)
    private String lastLevelCompleted;

    @Column(name = "last_level_date")
    private LocalDate lastLevelDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}