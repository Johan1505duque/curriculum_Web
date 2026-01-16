package com.hse.Curriculum.Models;

import com.hse.Curriculum.Enum.GraduateStatusEnum;
import com.hse.Curriculum.Converter.GraduateStatusConverter;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "education")
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Integer educationId;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code")
    private Country country;

    // ========== EDUCATION ==========


    @Column(name = "institution_name", length = 255)
    private String institutioName;

    @Column(name = "degree_title", length = 255)
    private String degreeTitle;

    @Column(name = "last_level_completed", length = 255)
    private String lastLevelCompleted;

    @Column(name = "graduate_status", columnDefinition = "graduate_status_enum DEFAULT 'no'")
    @Convert(converter = GraduateStatusConverter.class)
    private GraduateStatusEnum graduateStatus = GraduateStatusEnum.NO;

    //===========COMPLEMENT==========================
    @Column(name = "country_code", columnDefinition = "SMALLINT")
    private Short countryCode;

    // ========== FECHAS ==========

    @Column(name = "last_level_date", updatable = false)
    private LocalDate lastLevelDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
