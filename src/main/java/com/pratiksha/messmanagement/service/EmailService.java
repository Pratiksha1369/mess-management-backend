package com.pratiksha.messmanagement.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 🟢 OTP save karne ke liye temporary storage (Email -> OTP)
    private Map<String, String> otpStorage = new HashMap<>();

    // 1. OTP Generate aur Send karne ka function
    public String generateAndSendOtp(String email) {
        // 6-digit random OTP banao
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // OTP ko memory mein save karo
        otpStorage.put(email, otp);

        // Email bhejo
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("🔑 Password Reset OTP - Balanced Bowl Mess");
        message.setText("Hello,\n\nYour OTP for password reset is: " + otp + "\n\nPlease do not share this OTP with anyone.\n\nThanks,\nBalanced Bowl Team");
        
        mailSender.send(message);

        return "OTP sent successfully";
    }

    // 2. OTP Verify karne ka function
    public boolean verifyOtp(String email, String otp) {
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            otpStorage.remove(email); // OTP use ho gaya, ab isko delete kar do
            return true;
        }
        return false;
    }
    
 // 🟢 PURANA FUNCTION (Jo galti se delete ho gaya tha)
    public void sendReminderEmail(String email, String name, String msg) {
        org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Mess Subscription Update");
        message.setText("Hello " + name + ",\n\n" + msg + "\n\nRegards,\nBalanced Bowl Mess Team");
        
        mailSender.send(message);
    }
}