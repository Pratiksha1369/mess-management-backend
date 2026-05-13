package com.pratiksha.messmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mess_subscriptions")
public class MessSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A student can have one active subscription at a time
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // The predefined plan the student has purchased
    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private MessPlan messPlan;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer totalMeals; 
    
    private Integer remainingMeals; 

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status; 

    public enum SubscriptionStatus {
        ACTIVE,
        EXPIRED,
        GRACE_PERIOD 
    }

    // Getters and Setters 
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public MessPlan getMessPlan() { return messPlan; }
    public void setMessPlan(MessPlan messPlan) { this.messPlan = messPlan; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getTotalMeals() { return totalMeals; }
    public void setTotalMeals(Integer totalMeals) { this.totalMeals = totalMeals; }

    public Integer getRemainingMeals() { return remainingMeals; }
    public void setRemainingMeals(Integer remainingMeals) { this.remainingMeals = remainingMeals; }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
}