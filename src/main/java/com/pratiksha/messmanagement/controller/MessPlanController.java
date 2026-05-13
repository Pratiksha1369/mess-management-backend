package com.pratiksha.messmanagement.controller;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.pratiksha.messmanagement.entity.MessPlan;
import com.pratiksha.messmanagement.service.MessPlanService;

import org.springframework.web.bind.annotation.CrossOrigin;
import com.pratiksha.messmanagement.repository.MessPlanRepository;

@CrossOrigin(origins = "http://localhost:5173") // 🆕 Yeh line add karni hai
@RestController
@RequestMapping("/plans")
public class MessPlanController {
 // ... tumhara baaki purana code waise hi rahega ...

    @Autowired
    private MessPlanService messPlanService;

    @Autowired
    private MessPlanRepository messPlanRepository;
    // Naya plan add karne ke liye POST request
    @PostMapping
    public MessPlan addPlan(@RequestBody MessPlan plan) {
        return messPlanService.createPlan(plan);
    }

    // Saare plans dekhne ke liye GET request
    @GetMapping
    public List<MessPlan> getAllPlans() {
        return messPlanService.getAllPlans();
    }
    
 // 2. Galat plan delete karne ke liye
    @DeleteMapping("/{id}")
    public String deletePlan(@PathVariable Long id) {
        messPlanRepository.deleteById(id);
        return "Plan deleted successfully";
    }
    
 // 🟢 UPDATE API: Plan ko edit karne ke liye
    @PutMapping("/{id}")
    public MessPlan updatePlan(@PathVariable Long id, @RequestBody MessPlan updatedPlan) {
        updatedPlan.setId(id);
        return messPlanRepository.save(updatedPlan);
    }
    
    
    
}