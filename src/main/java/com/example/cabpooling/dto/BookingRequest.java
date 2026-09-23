package com.example.cabpooling.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingRequest {

    private Long employeeId;

    private String office;

    private LocalDateTime shiftTime;
}