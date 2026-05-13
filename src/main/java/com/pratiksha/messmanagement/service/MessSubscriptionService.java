package com.pratiksha.messmanagement.service;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.pratiksha.messmanagement.entity.MessPlan;
import com.pratiksha.messmanagement.entity.MessSubscription;
import com.pratiksha.messmanagement.entity.Student;
import com.pratiksha.messmanagement.repository.MessSubscriptionRepository;
import com.pratiksha.messmanagement.entity.MessSubscription.SubscriptionStatus;
import com.pratiksha.messmanagement.exception.ResourceNotFoundException;

@Service
public class MessSubscriptionService {

	@Autowired
	private MessSubscriptionRepository subscriptionRepository;
	@Autowired
	private EmailService emailService;

	// Yeh function tab call hoga jab bacha admission lega
	// Yeh function tab call hoga jab bacha admission lega
		public MessSubscription takeAdmission(Student student, MessPlan plan) {

			MessSubscription newSubscription = new MessSubscription();
			newSubscription.setStudent(student);
			newSubscription.setMessPlan(plan);

			LocalDate today = LocalDate.now();
			newSubscription.setStartDate(today); 
			newSubscription.setEndDate(today.plusMonths(1));

			// 🟢 UPDATE: Sunday Logic (30 days * 2 meals = 60. Minus 4 Sunday Dinners = 56 Meals)
			int totalTiffins = 56; 
			newSubscription.setTotalMeals(totalTiffins);
			newSubscription.setRemainingMeals(totalTiffins); 
			newSubscription.setStatus(SubscriptionStatus.ACTIVE);

			// Database mein save karo
			MessSubscription savedSubscription = subscriptionRepository.save(newSubscription);

	        // 🟢 UPDATE: Welcome Email Bhejna
			// 🟢 UPDATE: Professional Welcome Email
	        String welcomeMsg = "Dear " + student.getName() + ",\n\n" +
	                            "Welcome to the Mess Management System!\n\n" +
	                            "We are pleased to inform you that your subscription for the '" + plan.getName() + "' has been successfully activated.\n\n" +
	                            "Subscription Details:\n" +
	                            "• Start Date: " + today + "\n" +
	                            "• End Date: " + today.plusMonths(1) + "\n" +
	                            "• Total Meals Allocated: " + totalTiffins + "\n\n" +
	                            "You can access your Digital Meal Pass (QR Code) directly from your student dashboard. Please present this QR code at the mess counter to consume your daily meals.\n\n" +
	                            "We hope you enjoy your meals!\n\n" +
	                            "Best Regards,\n" +
	                            "Mess Administration Team";
	        // Async mail bhejna taaki website slow na ho
	        new Thread(() -> {
	            emailService.sendReminderEmail(student.getEmail(), student.getName(), welcomeMsg);
	        }).start();

			return savedSubscription;
		}
	// Naye imports add karna mat bhulna upar file mein:

	// ... (takeAdmission method yahan hai) ...

	// QR Scan hone par meal cut karne ka logic
	// QR Scan hone par meal cut karne ka aur Validity check karne ka logic

	public String consumeMeal(Long subscriptionId) {
		MessSubscription subscription = subscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

		LocalDate today = LocalDate.now();

		// 1. CHALO CHECK KAREIN KI KYA 1 MAHINA PHOORA HO GAYA HAI?
		if (today.isAfter(subscription.getEndDate())) {

			long daysPassed = ChronoUnit.DAYS.between(subscription.getEndDate(), today);

			if (daysPassed <= 10) {
				// Agar status PEHLI BAAR Grace Period mein jaa raha hai
				if (subscription.getStatus() != SubscriptionStatus.GRACE_PERIOD) {
					subscription.setStatus(SubscriptionStatus.GRACE_PERIOD);
					subscriptionRepository.save(subscription);

					// 🚀 AUTOMATED EMAIL FOR GRACE PERIOD
					String msg = "Your 1-month mess subscription validity has ended, but you still have remaining meals. Your 10-day Grace Period has now started. Please consume your remaining meals within this period!";
					emailService.sendReminderEmail(subscription.getStudent().getEmail(),
							subscription.getStudent().getName(), msg);
				}
			} else {
				// Agar status PEHLI BAAR Expired ho raha hai
				if (subscription.getStatus() != SubscriptionStatus.EXPIRED) {
					subscription.setStatus(SubscriptionStatus.EXPIRED);
					subscriptionRepository.save(subscription);

					// 🚀 AUTOMATED EMAIL FOR EXPIRATION
					String msg = "Your mess subscription and the 10-day grace period have completely expired. To continue enjoying our daily meals, please renew your plan from the dashboard.";
					emailService.sendReminderEmail(subscription.getStudent().getEmail(),
							subscription.getStudent().getName(), msg);
				}
				return "Your plan has completely expired (10 days grace period over)! Please renew.";
			}
		}

		// 2. EXPIRED PLAN CHECK
		if (subscription.getStatus() == SubscriptionStatus.EXPIRED) {
			return "Your plan has expired! Please renew.";
		}

		// 3. MEALS KHARE HAIN YA KHATAM HO GAYE?
		if (subscription.getRemainingMeals() <= 0) {
			return "You have 0 meals left!";
		}

		// 4. SUNDAY NIGHT HOLIDAY RULE
		LocalDateTime now = LocalDateTime.now();
		if (now.getDayOfWeek() == DayOfWeek.SUNDAY && now.getHour() >= 17) {
			return "Mess is closed on Sunday night. Enjoy your holiday!";
		}

		// 5. SAB PERFECT HAI! 1 Meal minus kar do
		subscription.setRemainingMeals(subscription.getRemainingMeals() - 1);
		subscriptionRepository.save(subscription);

		return "Meal approved! Remaining meals: " + subscription.getRemainingMeals() + " (Status: "
				+ subscription.getStatus() + ")";
	}
	
	// 🟢 NAYA FUNCTION: Emergency Holiday Broadcast ke liye
		public String broadcastHoliday(String date, String reason) {
			// 1. Saare ACTIVE bachon ko dhoondho
			List<MessSubscription> activeSubs = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);
			
			int count = 0;

			// 2. Loop lagao aur sabko mail bhejo
			for (MessSubscription sub : activeSubs) {
				Student student = sub.getStudent();
				if (student != null) {
					
					// Email ka message design
					String msg = "🚨 URGENT UPDATE: MESS HOLIDAY 🚨\n\n" +
					             "Dear " + student.getName() + ",\n\n" +
					             "This is an emergency broadcast to inform you that the mess will remain CLOSED on " + date + ".\n\n" +
					             "Reason: " + reason + "\n\n" +
					             "Please make alternative arrangements for your meals on this day. We apologize for the inconvenience.\n\n" +
					             "Regards,\nMess Administration Team";
					
					// Async Mail bhejna (taaki app hang na ho)
					new Thread(() -> {
						emailService.sendReminderEmail(student.getEmail(), student.getName(), msg);
					}).start();
					
					count++;
				}
			}
			
			return "✅ Broadcast success! Emergency email sent to " + count + " active students.";
		}
	
	// Naya function student ki details lane ke liye
    public List<MessSubscription> getSubscriptionsByStudent(Long studentId) {
        return subscriptionRepository.findByStudentId(studentId);
    }
    
 // 🟢 NAYA FUNCTION: Dashboard Analytics ke liye
 	public java.util.Map<String, Integer> getAnalytics() {
 		// Sabhi ACTIVE bachon ko dhoondho
 		List<MessSubscription> activeSubs = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);
 		
 		int vegCount = 0;
 		int nonVegCount = 0;

 		// Loop lagakar check karo kaun veg hai aur kaun non-veg
 		for (MessSubscription sub : activeSubs) {
 			if (sub.getMessPlan() != null) {
 				if (sub.getMessPlan().getType().name().equals("NON_VEG")) {
 					nonVegCount++;
 				} else {
 					vegCount++;
 				}
 			}
 		}

 		// Data ko ek Map mein pack karke bhej do
 		java.util.Map<String, Integer> stats = new java.util.HashMap<>();
 		stats.put("totalActive", activeSubs.size());
 		stats.put("vegTiffins", vegCount);
 		stats.put("nonVegTiffins", nonVegCount);
 		
 		return stats;
 	}
 	
 	
 // ==========================================
    // UPDATE SUBSCRIPTION (Remaining Meals & Status)
    // ==========================================
    public MessSubscription updateSubscription(Long subId, int newRemainingMeals, String newStatus) {
        // 1. First, find the existing subscription in the database using the ID
        MessSubscription subscription = subscriptionRepository.findById(subId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subId));

        // 2. Update the fields with the new values provided by the Admin
        subscription.setRemainingMeals(newRemainingMeals);
        
        // Convert the String status (e.g., "SUSPENDED") back into our Enum type
        subscription.setStatus(SubscriptionStatus.valueOf(newStatus));

        // 3. Save the updated subscription back to the database
        // Note: In Spring Data JPA, .save() updates the record if the ID already exists!
        return subscriptionRepository.save(subscription);
    }
}