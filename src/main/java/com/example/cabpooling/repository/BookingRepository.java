package com.example.cabpooling.repository;

import com.example.cabpooling.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByOfficeAndShiftTimeAndStatus(
            String office,
            LocalDateTime shiftTime,
            String status
    );

    Optional<Object> findByEmployeeIdAndShiftTime(Long employeeId, LocalDateTime shiftTime);
}