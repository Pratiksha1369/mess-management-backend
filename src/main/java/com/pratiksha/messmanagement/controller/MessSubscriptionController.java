package com.pratiksha.messmanagement.controller;

import java.util.List;
import java.util.Map; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*; // Is * ki wajah se aur import nahi chahiye

import com.pratiksha.messmanagement.entity.MessPlan;
import com.pratiksha.messmanagement.entity.MessSubscription;
import com.pratiksha.messmanagement.entity.Student;
import com.pratiksha.messmanagement.exception.ResourceNotFoundException;
import com.pratiksha.messmanagement.repository.MessPlanRepository;
import com.pratiksha.messmanagement.repository.StudentRepository;
import com.pratiksha.messmanagement.service.MessSubscriptionService;

@CrossOrigin(origins = "http://localhost:5173") 
@RestController
@RequestMapping("/subscriptions")
public class MessSubscriptionController {

    @Autowired
    private MessSubscriptionService subscriptionService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MessPlanRepository planRepository;

    // Admission lene ke liye POST API
    @PostMapping("/join")
    public MessSubscription joinMess(@RequestParam Long studentId, @RequestParam Long planId) {
        
        // 1. Pehle database se check karo ki Student exist karta hai ya nahi
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        // 2. Phir check karo ki Mess Plan exist karta hai ya nahi
        MessPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + planId));

        // 3. Agar dono mil gaye, toh Service wala logic call kardo (jisme dates aur meals set hote hain)
        return subscriptionService.takeAdmission(student, plan);
    }
    
 // QR Code scan hone par yeh API hit hogi
    @PostMapping("/{id}/scan")
    public String scanQrCode(@PathVariable Long id) {
        return subscriptionService.consumeMeal(id);
    }
    
 // Naya function student ki details lane ke liye
 // Ek particular student ki subscription details lane ke liye
    @GetMapping("/student/{studentId}")
    public List<MessSubscription> getStudentSubscriptions(@PathVariable Long studentId) {
        return subscriptionService.getSubscriptionsByStudent(studentId);
    }
    
 // 🟢 NAYA API: QR Scan / Meal Deduct karne ke liye
 	@PostMapping("/consume/{subscriptionId}")
 	public String consumeMeal(@PathVariable Long subscriptionId) {
 		try {
 			// Yeh seedha tumhare service wale function ko call karega
 			return subscriptionService.consumeMeal(subscriptionId);
 		} catch (Exception e) {
 			return "Error: " + e.getMessage();
 		}
 	}


	// 🟢 NAYI API: Broadcast trigger karne ke liye
	@PostMapping("/broadcast-holiday")
	public String broadcastHoliday(@RequestBody Map<String, String> payload) {
		String date = payload.get("date");
		String reason = payload.get("reason");
		
		try {
			return subscriptionService.broadcastHoliday(date, reason);
		} catch (Exception e) {
			return "❌ Error sending broadcast: " + e.getMessage();
		}
	}
	
	// 🟢 NAYI API: Admin Dashboard Cards ke liye
		@GetMapping("/analytics")
		public java.util.Map<String, Integer> getDashboardAnalytics() {
			return subscriptionService.getAnalytics(); // Agar service ka naam messSubscriptionService hai, toh wahi likhna
		}
		
		// 🟢 UPDATE API: To allow Admin to manually adjust meals or block a student
	    @PutMapping("/update/{subId}")
	    public MessSubscription updateSubscription(
	            @PathVariable Long subId, 
	            @RequestBody Map<String, String> payload) {
	        
	        // Extract the data sent from the React frontend
	        int remainingMeals = Integer.parseInt(payload.get("remainingMeals"));
	        String status = payload.get("status");
	        
	        // Call the service method we just created
	        return subscriptionService.updateSubscription(subId, remainingMeals, status);
	    }
}

