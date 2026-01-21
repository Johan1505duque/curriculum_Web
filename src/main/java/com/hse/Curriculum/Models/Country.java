package com.hse.Curriculum.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "country_code", length = 100)
    private String countryCode;


    @Column(name = "country_name", length = 100)
    private String countryName;

}
