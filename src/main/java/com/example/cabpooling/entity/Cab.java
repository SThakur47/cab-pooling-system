package com.example.cabpooling.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cabs")
public class Cab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private String office;

    @Column(nullable = false)
    private String shiftTime;

    public Cab(Integer capacity, String office, String shiftTime) {
        this.capacity = capacity;
        this.office = office;
        this.shiftTime = shiftTime;
    }
}