package com.hse.Curriculum.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "municipality")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Municipality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "municipality_id")
    private Integer municipalityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "dane_code", length = 10)
    private String daneCode;

    @Column(name = "is_capital", nullable = false)
    private Boolean isCapital = false;

}
