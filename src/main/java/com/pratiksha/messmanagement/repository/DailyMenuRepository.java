package com.pratiksha.messmanagement.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pratiksha.messmanagement.entity.DailyMenu;
import com.pratiksha.messmanagement.enums.PlanType;

public interface DailyMenuRepository extends JpaRepository<DailyMenu, Long> {
    
	// Purana: Optional<DailyMenu> findByMenuDateAndPlanType(...)
	// NAYA CODE:
	Optional<DailyMenu> findByDateAndPlanType(java.time.LocalDate date, com.pratiksha.messmanagement.enums.PlanType planType);
}