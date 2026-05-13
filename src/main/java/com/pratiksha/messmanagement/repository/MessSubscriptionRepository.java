package com.pratiksha.messmanagement.repository;

import java.util.List; // Naya Import

import org.springframework.data.jpa.repository.JpaRepository;
import com.pratiksha.messmanagement.entity.MessSubscription;
import com.pratiksha.messmanagement.entity.MessSubscription.SubscriptionStatus;

public interface MessSubscriptionRepository extends JpaRepository<MessSubscription, Long> {
    
    // ✅ Custom Query: Ek student ki saari subscriptions dhoondhne ke liye
    List<MessSubscription> findByStudentId(Long studentId);
    List<MessSubscription> findByStatus(SubscriptionStatus status);
}