package com.pratiksha.messmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pratiksha.messmanagement.entity.Student;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    //for admin dashboard search bar
    List<Student> findByNameContainingIgnoreCase(String name);
}