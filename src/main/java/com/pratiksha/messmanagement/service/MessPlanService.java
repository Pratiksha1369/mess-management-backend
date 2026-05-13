package com.pratiksha.messmanagement.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pratiksha.messmanagement.entity.MessPlan;
import com.pratiksha.messmanagement.repository.MessPlanRepository;

@Service
public class MessPlanService {

    @Autowired
    private MessPlanRepository messPlanRepository;

    // Naya plan add karne ka method
    public MessPlan createPlan(MessPlan plan) {
        return messPlanRepository.save(plan);
    }

    // Database mein save hue saare plans dekhne ka method
    public List<MessPlan> getAllPlans() {
        return messPlanRepository.findAll();
    }
    
    
}