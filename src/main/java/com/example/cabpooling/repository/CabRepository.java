package com.example.cabpooling.repository;

import com.example.cabpooling.entity.Cab;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CabRepository extends JpaRepository<Cab, Long> {
}