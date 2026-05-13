package com.pratiksha.messmanagement.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.pratiksha.messmanagement.dto.StudentDTO;
import com.pratiksha.messmanagement.entity.MessSubscription;
import com.pratiksha.messmanagement.entity.Student;
import com.pratiksha.messmanagement.payload.PageResponse;
import com.pratiksha.messmanagement.service.StudentService;
import com.pratiksha.messmanagement.repository.MessSubscriptionRepository;
import com.pratiksha.messmanagement.repository.StudentRepository;
import com.pratiksha.messmanagement.service.EmailService;

// Allows React frontend (port 5173) to communicate securely
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private MessSubscriptionRepository subscriptionRepository;
    
    @Autowired
    private EmailService emailService;

    // Create Student
    @PostMapping
    public StudentDTO createStudent(@Valid @RequestBody StudentDTO dto) {
        return studentService.saveStudent(dto);
    }
    
    // Create Multiple Students (Bulk Add)
    @PostMapping("/bulk")
    public List<StudentDTO> createMultipleStudents(@Valid @RequestBody List<StudentDTO> dtos) {
        return studentService.saveAllStudents(dtos);
    }

    // Get All Students WITH Pagination 
    @GetMapping
    public PageResponse<StudentDTO> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return studentService.getStudentsWithPagination(page, size);
    }

    // Get Student By ID
    @GetMapping("/{id}")
    public StudentDTO getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    // Search Students
    @GetMapping("/search")
    public List<StudentDTO> searchStudents(@RequestParam String name) {
        return studentService.searchStudentsByName(name);
    }

    // Update Student
    @PutMapping("/{id}")
    public StudentDTO updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO dto) {
        return studentService.updateStudent(id, dto);
    }

    // Secure Delete Function
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        try {
            List<MessSubscription> studentPlans = subscriptionRepository.findByStudentId(id);
            
            // Safety Check: Prevent deletion if the plan is ACTIVE
            for (MessSubscription plan : studentPlans) {
                if (plan.getStatus().name().equals("ACTIVE")) {
                    return "ERROR: Cannot delete! This student has an ACTIVE mess plan.";
                }
            }
            
            // Proceed to delete if not ACTIVE (Inactive/Expired)
            if (studentPlans != null && !studentPlans.isEmpty()) {
                subscriptionRepository.deleteAll(studentPlans);
            }
            studentRepository.deleteById(id);
            
            return "SUCCESS: Student deleted successfully!";
            
        } catch (Exception e) {
            return "ERROR: Could not delete student. " + e.getMessage();
        }
    }
    
    // Login API (Authenticates against the Database)
    @PostMapping("/login")
    public ResponseEntity<Object> loginStudent(@RequestBody Map<String, String> credentials) {
        
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<Student> studentOpt = studentRepository.findByEmail(email);

        if (studentOpt.isPresent() && studentOpt.get().getPassword().equals(password)) {
            // Sending User Data as JSON upon successful login
            Map<String, Object> responseData = new java.util.HashMap<>();
            responseData.put("id", studentOpt.get().getId());
            responseData.put("name", studentOpt.get().getName());
            responseData.put("email", studentOpt.get().getEmail());
            
            return ResponseEntity.ok(responseData);
        } else {
            Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", "Invalid Email or Password");
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
    
    // API to send OTP for forgotten password
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        // First, verify if the email exists in the database
        Student student = studentRepository.findByEmail(email).orElse(null);        
        if(student == null) {
            return "ERROR: No student found with this email!";
        }
        
        emailService.generateAndSendOtp(email);
        return "SUCCESS: OTP sent to your email!";
    }

    // API to verify OTP and reset the password
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        // Verify the OTP
        boolean isValid = emailService.verifyOtp(email, otp);
        if(!isValid) {
            return "ERROR: Invalid or Expired OTP!";
        }
        
        // Save the new password if OTP is valid
        Student student = studentRepository.findByEmail(email).orElse(null);
        if(student != null) {
            student.setPassword(newPassword);
            studentRepository.save(student);
            return "SUCCESS: Password reset successfully!";
        }
        return "ERROR: Student not found!";
    }
}