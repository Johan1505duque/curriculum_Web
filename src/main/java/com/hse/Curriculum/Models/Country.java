package com.hse.Curriculum.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "countries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    private Integer countryId;

    @Column(name = "country_code", nullable = false, unique = true)
    private Integer countryCode;

    @Column(name = "country_name", nullable = false, length = 100)
    private String countryName;

    @Column(name = "iso_code_2", length = 2, unique = true)
    private String isoCode2;

    @Column(name = "iso_code_3", length = 3, unique = true)
    private String isoCode3;

    @Column(name = "phone_code", length = 10)
    private String phoneCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}