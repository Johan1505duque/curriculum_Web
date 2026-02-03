package com.hse.Curriculum.Models;

import com.hse.Curriculum.Enum.EducationTypeEnum;
import com.hse.Curriculum.Enum.GraduateStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "type_education", nullable = false)
    private EducationTypeEnum typeEducation;

    @Column(name = "academic_level", length = 100)
    private String academicLevel;

    @Column(name = "institution_name", length = 200)
    private String institutionName;

    @Column(name = "degree_title", length = 255)
    private String degreeTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(name = "graduate_status", nullable = false)
    private GraduateStatusEnum graduateStatus = GraduateStatusEnum.NO;

    @Column(name = "last_level_completed", length = 100)
    private String lastLevelCompleted;

    @Column(name = "last_level_date")
    private LocalDate lastLevelDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}