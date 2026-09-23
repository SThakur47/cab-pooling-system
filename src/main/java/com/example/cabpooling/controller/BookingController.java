package com.example.cabpooling.controller;

import com.example.cabpooling.dto.BookingRequest;
import com.example.cabpooling.entity.Booking;
import com.example.cabpooling.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(
            @RequestBody BookingRequest request) {

        return bookingService.createBooking(request);
    }

    @PutMapping("/{bookingId}/cancel")
    public Booking cancelBooking(@PathVariable Long bookingId) {
        return bookingService.cancelBooking(bookingId);
    }
}