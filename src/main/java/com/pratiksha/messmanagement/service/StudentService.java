package com.pratiksha.messmanagement.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pratiksha.messmanagement.dto.StudentDTO;
import com.pratiksha.messmanagement.entity.Student;
import com.pratiksha.messmanagement.exception.DuplicateResourceException;
import com.pratiksha.messmanagement.exception.ResourceNotFoundException;
import com.pratiksha.messmanagement.payload.PageResponse;
import com.pratiksha.messmanagement.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // Convert Entity to DTO
    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setAddress(student.getAddress());
        return dto;
    }

    // Convert DTO to Entity
    private Student convertToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPassword(dto.getPassword()); 
        student.setPhone(dto.getPhone());
        student.setAddress(dto.getAddress());
        return student;
    }

    // Create a new Student
    public StudentDTO saveStudent(StudentDTO dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        Student student = convertToEntity(dto);
        Student savedStudent = studentRepository.save(student);
        return convertToDTO(savedStudent);
    }
    
    // Create Multiple Students (Bulk Add)
    public List<StudentDTO> saveAllStudents(List<StudentDTO> studentDTOs) {
        return studentDTOs.stream()
                .map(this::saveStudent)
                .collect(Collectors.toList());
    }

    // Get All Students
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get Student By ID
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return convertToDTO(student);
    }

    // Update Student Details
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        existingStudent.setName(dto.getName());
        existingStudent.setEmail(dto.getEmail());
        existingStudent.setPhone(dto.getPhone());
        existingStudent.setAddress(dto.getAddress());

        Student updatedStudent = studentRepository.save(existingStudent);
        return convertToDTO(updatedStudent);
    }

    // Delete Student
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }
    
    // Search Students by Name
    public List<StudentDTO> searchStudentsByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Get Students with Pagination Support
    public PageResponse<StudentDTO> getStudentsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentsPage = studentRepository.findAll(pageable);

        List<StudentDTO> studentDTOs = studentsPage.getContent()
                            .stream()
                            .map(this::convertToDTO)
                            .toList();

        return new PageResponse<>(
                studentDTOs,
                studentsPage.getNumber(),
                studentsPage.getTotalElements(),
                studentsPage.getTotalPages()
        );
    }
}