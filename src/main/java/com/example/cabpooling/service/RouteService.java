package com.example.cabpooling.service;

import com.example.cabpooling.dto.RouteRequest;
import com.example.cabpooling.entity.Booking;
import com.example.cabpooling.entity.Cab;
import com.example.cabpooling.entity.CabAssignment;
import com.example.cabpooling.repository.BookingRepository;
import com.example.cabpooling.repository.CabAssignmentRepository;
import com.example.cabpooling.repository.CabRepository;
import com.example.cabpooling.util.DistanceUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RouteService {

    private static final double AVERAGE_SPEED_KMH = 30.0;

    private final BookingRepository bookingRepository;
    private final CabRepository cabRepository;
    private final CabAssignmentRepository assignmentRepository;

    public RouteService(
            BookingRepository bookingRepository,
            CabRepository cabRepository,
            CabAssignmentRepository assignmentRepository) {

        this.bookingRepository = bookingRepository;
        this.cabRepository = cabRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public List<CabAssignment> generateRoutes(RouteRequest request) {

        List<Booking> bookings =
                bookingRepository.findByOfficeAndShiftTimeAndStatus(
                        request.getOffice(),
                        request.getShiftTime(),
                        "CONFIRMED"
                );

        if (bookings.isEmpty()) {
            throw new RuntimeException("No confirmed bookings found");
        }

        List<Booking> remaining = new ArrayList<>(bookings);
        List<CabAssignment> result = new ArrayList<>();

        while (!remaining.isEmpty()) {

            List<Booking> group = createGroup(
                    remaining,
                    request.getCabCapacity()
            );

            List<Booking> route = optimizeRoute(
                    group,
                    request.getOfficeLatitude(),
                    request.getOfficeLongitude()
            );

            validateRideTime(
                    route,
                    request.getOfficeLatitude(),
                    request.getOfficeLongitude(),
                    request.getMaxRideTimeMinutes()
            );

            validateNightSafety(
                    route,
                    Boolean.TRUE.equals(request.getNightShift())
            );

            Cab cab = new Cab(
                    request.getCabCapacity(),
                    request.getOffice(),
                    request.getShiftTime().toString()
            );

            cab = cabRepository.save(cab);

            for (int i = 0; i < route.size(); i++) {

                Booking booking = route.get(i);

                CabAssignment assignment = new CabAssignment();

                assignment.setCab(cab);
                assignment.setBooking(booking);
                assignment.setPickupOrder(i + 1);

                double rideTime = calculateRideTime(
                        route,
                        i,
                        request.getOfficeLatitude(),
                        request.getOfficeLongitude()
                );

                assignment.setRideTimeMinutes(rideTime);

                double pickupEta = calculatePickupEta(
                        route,
                        i
                );

                assignment.setPickupEtaMinutes(pickupEta);

                result.add(
                        assignmentRepository.save(assignment)
                );
            }
        }

        return result;
    }

    private List<Booking> createGroup(
            List<Booking> remaining,
            int capacity) {

        List<Booking> group = new ArrayList<>();

        Booking first = remaining.get(0);
        group.add(first);

        remaining.remove(first);

        while (group.size() < capacity && !remaining.isEmpty()) {

            Booking nearest = remaining.stream()
                    .min(Comparator.comparingDouble(
                            booking -> distanceFromGroup(
                                    booking,
                                    group
                            )
                    ))
                    .orElse(null);

            if (nearest == null) {
                break;
            }

            group.add(nearest);
            remaining.remove(nearest);
        }

        return group;
    }

    private double distanceFromGroup(
            Booking candidate,
            List<Booking> group) {

        double minimumDistance = Double.MAX_VALUE;

        for (Booking booking : group) {

            double distance = DistanceUtil.calculateDistance(
                    candidate.getEmployee().getLatitude(),
                    candidate.getEmployee().getLongitude(),
                    booking.getEmployee().getLatitude(),
                    booking.getEmployee().getLongitude()
            );

            minimumDistance =
                    Math.min(minimumDistance, distance);
        }

        return minimumDistance;
    }

    private List<Booking> optimizeRoute(
            List<Booking> bookings,
            double officeLat,
            double officeLon) {

        List<Booking> unvisited = new ArrayList<>(bookings);
        List<Booking> route = new ArrayList<>();

        Booking current = unvisited.remove(0);
        route.add(current);

        while (!unvisited.isEmpty()) {

            Booking nearest = null;
            double shortestDistance = Double.MAX_VALUE;

            for (Booking booking : unvisited) {

                double distance = DistanceUtil.calculateDistance(
                        current.getEmployee().getLatitude(),
                        current.getEmployee().getLongitude(),
                        booking.getEmployee().getLatitude(),
                        booking.getEmployee().getLongitude()
                );

                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nearest = booking;
                }
            }

            if (nearest == null) {
                break;
            }

            route.add(nearest);
            unvisited.remove(nearest);

            current = nearest;
        }

        return route;
    }

    private double calculateRideTime(
            List<Booking> route,
            int pickupIndex,
            double officeLat,
            double officeLon) {

        double totalDistance = 0;

        for (int i = pickupIndex; i < route.size() - 1; i++) {

            Booking current = route.get(i);
            Booking next = route.get(i + 1);

            totalDistance += DistanceUtil.calculateDistance(
                    current.getEmployee().getLatitude(),
                    current.getEmployee().getLongitude(),
                    next.getEmployee().getLatitude(),
                    next.getEmployee().getLongitude()
            );
        }

        Booking last = route.get(route.size() - 1);

        totalDistance += DistanceUtil.calculateDistance(
                last.getEmployee().getLatitude(),
                last.getEmployee().getLongitude(),
                officeLat,
                officeLon
        );

        return (totalDistance / AVERAGE_SPEED_KMH) * 60;
    }

    private void validateRideTime(
            List<Booking> route,
            double officeLat,
            double officeLon,
            double maxRideTime) {

        for (int i = 0; i < route.size(); i++) {

            double rideTime = calculateRideTime(
                    route,
                    i,
                    officeLat,
                    officeLon
            );

            if (rideTime > maxRideTime) {
                throw new RuntimeException(
                        "Route rejected: maximum ride time exceeded"
                );
            }
        }
    }

    private void validateNightSafety(
            List<Booking> route,
            boolean nightShift) {

        if (!nightShift || route.isEmpty()) {
            return;
        }

        Booking first = route.get(0);
        Booking last = route.get(route.size() - 1);

        if ("FEMALE".equalsIgnoreCase(
                first.getEmployee().getGender())) {

            throw new RuntimeException(
                    "Night safety violation: female employee cannot be first pickup");
        }

        if ("FEMALE".equalsIgnoreCase(
                last.getEmployee().getGender())) {

            throw new RuntimeException(
                    "Night safety violation: female employee cannot be last drop");
        }
    }

    private double calculatePickupEta(
            List<Booking> route,
            int pickupIndex) {

        double totalDistance = 0;

        // Start from the first pickup
        // and calculate time until this pickup
        for (int i = 0; i < pickupIndex; i++) {

            Booking current = route.get(i);
            Booking next = route.get(i + 1);

            totalDistance += DistanceUtil.calculateDistance(
                    current.getEmployee().getLatitude(),
                    current.getEmployee().getLongitude(),
                    next.getEmployee().getLatitude(),
                    next.getEmployee().getLongitude()
            );
        }

        return (totalDistance / AVERAGE_SPEED_KMH) * 60;
    }
}
