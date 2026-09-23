package com.example.cabpooling.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cab_assignments")
public class CabAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cab_id", nullable = false)
    private Cab cab;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Integer pickupOrder;

    @Column(nullable = false)
    private Double pickupEtaMinutes;

    @Column(nullable = false)
    private Double rideTimeMinutes;
}