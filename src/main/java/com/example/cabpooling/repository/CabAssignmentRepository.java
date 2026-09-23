package com.example.cabpooling.repository;

import com.example.cabpooling.entity.CabAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CabAssignmentRepository
        extends JpaRepository<CabAssignment, Long> {

    List<CabAssignment> findByCabIdOrderByPickupOrder(Long cabId);
}