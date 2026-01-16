package com.hse.Curriculum.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_code")
    private Integer countryCode;

    @Column(name = "country_name", length = 100)
    private String countryName;

}
