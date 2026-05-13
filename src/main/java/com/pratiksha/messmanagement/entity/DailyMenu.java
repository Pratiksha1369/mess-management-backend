package com.pratiksha.messmanagement.entity;

import java.time.LocalDate;
import com.pratiksha.messmanagement.enums.PlanType;
import jakarta.persistence.*;

@Entity
@Table(name = "daily_menus")
public class DailyMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; 
    private String lunch;
    private String dinner;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLunch() { return lunch; }
    public void setLunch(String lunch) { this.lunch = lunch; }

    public String getDinner() { return dinner; }
    public void setDinner(String dinner) { this.dinner = dinner; }

    public PlanType getPlanType() { return planType; }
    public void setPlanType(PlanType planType) { this.planType = planType; }
}