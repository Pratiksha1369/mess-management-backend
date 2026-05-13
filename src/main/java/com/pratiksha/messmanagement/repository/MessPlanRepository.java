package com.pratiksha.messmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pratiksha.messmanagement.entity.MessPlan;

public interface MessPlanRepository extends JpaRepository<MessPlan, Long> {
}