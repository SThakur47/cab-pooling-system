package com.example.cabpooling.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RouteRequest {

    private String office;

    private LocalDateTime shiftTime;

    private Integer cabCapacity;

    private Double officeLatitude;

    private Double officeLongitude;

    private Double maxRideTimeMinutes;

    private Boolean nightShift;
}