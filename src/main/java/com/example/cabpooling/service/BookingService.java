package com.example.cabpooling.service;

import com.example.cabpooling.dto.BookingRequest;
import com.example.cabpooling.entity.Booking;
import com.example.cabpooling.entity.Employee;
import com.example.cabpooling.repository.BookingRepository;
import com.example.cabpooling.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EmployeeRepository employeeRepository;

    public BookingService(
            BookingRepository bookingRepository,
            EmployeeRepository employeeRepository) {
        this.bookingRepository = bookingRepository;
        this.employeeRepository = employeeRepository;
    }

    public Booking createBooking(BookingRequest request) {

        Employee employee = employeeRepository
                .findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        bookingRepository
                .findByEmployeeIdAndShiftTime(
                        request.getEmployeeId(),
                        request.getShiftTime())
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "Employee already has a booking for this shift");
                });

        Booking booking = new Booking();

        booking.setEmployee(employee);
        booking.setOffice(request.getOffice());
        booking.setShiftTime(request.getShiftTime());
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus("CANCELLED");

        return bookingRepository.save(booking);
    }
}