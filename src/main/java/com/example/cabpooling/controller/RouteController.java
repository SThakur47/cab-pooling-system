package com.example.cabpooling.controller;

import com.example.cabpooling.dto.RouteRequest;
import com.example.cabpooling.entity.CabAssignment;
import com.example.cabpooling.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/generate")
    public List<CabAssignment> generateRoute(
            @RequestBody RouteRequest request) {

        return routeService.generateRoutes(request);
    }
}
