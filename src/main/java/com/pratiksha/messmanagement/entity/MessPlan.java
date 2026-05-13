package com.pratiksha.messmanagement.entity;

import com.pratiksha.messmanagement.entity.Student.Gender;
import com.pratiksha.messmanagement.enums.PlanType; 

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MessPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; 

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private PlanType type; 

    private Double price;

    private String nonVegDays; 

    // Getters and Setters
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public PlanType getType() { return type; }
    public void setType(PlanType type) { this.type = type; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getNonVegDays() { return nonVegDays; }
    public void setNonVegDays(String nonVegDays) { this.nonVegDays = nonVegDays; }
}