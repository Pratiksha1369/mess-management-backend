package com.pratiksha.messmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.pratiksha.messmanagement.service.EmailService;
import com.pratiksha.messmanagement.repository.MessSubscriptionRepository;
import com.pratiksha.messmanagement.entity.MessSubscription;
import com.pratiksha.messmanagement.entity.Student;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173") // 👉 React ko allow karne ke liye
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private MessSubscriptionRepository subscriptionRepository;

    // Purana test email wala API
    @PostMapping("/test")
    public String testEmail(@RequestParam String email, @RequestParam String name) {
        String msg = "Yeh ek test email hai tumhare Mess Management System se! Agar tumhe yeh message mila, toh matlab tumhara email setup 100% successful hai. 🚀";
        emailService.sendReminderEmail(email, name, msg);
        return "Test email sent successfully to: " + email;
    }

    // 🔴 NAYI API: React ke "Emergency Broadcast" button ke liye 🔴
    @PostMapping("/broadcast")
    public String broadcastMessage(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        int count = 0;

        // 1. Saare ACTIVE bachon ko database se dhoondho
        List<MessSubscription> activeSubs = subscriptionRepository.findByStatus(MessSubscription.SubscriptionStatus.ACTIVE);

        // 2. Har bache ko loop mein mail bhejo
        for (MessSubscription sub : activeSubs) {
            Student student = sub.getStudent();
            if (student != null && student.getEmail() != null) {
                
            	String fullMessage = "Dear " + student.getName() + ",\n\n" +
                        "This is an important notification regarding your Balanced Bowl Mess Subscription.\n\n" +
                        "📢 MESSAGE FROM ADMIN:\n" +
                        "-------------------------------------------------\n" +
                        message + "\n" +
                        "-------------------------------------------------\n\n" +
                        "Please make a note of this update. If you have any urgent questions, please contact the mess counter.\n\n" +
                        "Warm Regards,\n" +
                        "Balanced Bowl Mess Administration Team\n" +
                        "Pune, Maharashtra";
                
                // Naye thread me mail bhejenge taaki application hang na ho
                new Thread(() -> {
                    emailService.sendReminderEmail(student.getEmail(), student.getName(), fullMessage);
                }).start();
                
                count++;
            }
        }
        
        return "Alert sent to " + count + " active students!";
    }
}