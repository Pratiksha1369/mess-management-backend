package com.pratiksha.messmanagement.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

@Service
public class EmailService {

    private static final String BREVO_API_KEY = System.getenv("BREVO_API_KEY");
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";
    
    private Map<String, String> otpStorage = new HashMap<>();

    private void sendEmail(String toEmail, String toName, String subject, String body) {
        try {
            String json = "{"
                + "\"sender\":{\"name\":\"Balanced Bowl Mess\",\"email\":\"balancedbowlmess@gmail.com\"},"
                + "\"to\":[{\"email\":\"" + toEmail + "\",\"name\":\"" + toName + "\"}],"
                + "\"subject\":\"" + subject + "\","
                + "\"textContent\":\"" + body.replace("\n", "\\n").replace("\"", "\\\"") + "\""
                + "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BREVO_URL))
                .header("Content-Type", "application/json")
                .header("api-key", BREVO_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Brevo response: " + response.statusCode() + " - " + response.body());
        } catch (Exception e) {
            System.err.println("Email send error: " + e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    public String generateAndSendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);

        String body = "Hello,\n\nYour OTP for password reset is: " + otp + "\n\nPlease do not share this OTP with anyone.\n\nThanks,\nBalanced Bowl Team";
        sendEmail(email, "Student", "Password Reset OTP - Balanced Bowl Mess", body);

        return "OTP sent successfully";
    }

    public boolean verifyOtp(String email, String otp) {
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            otpStorage.remove(email);
            return true;
        }
        return false;
    }

    public void sendReminderEmail(String email, String name, String msg) {
        sendEmail(email, name, "Mess Subscription Update", "Hello " + name + ",\n\n" + msg + "\n\nRegards,\nBalanced Bowl Mess Team");
    }
}