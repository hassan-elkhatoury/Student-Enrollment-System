package com.demo.student.repository;

import com.demo.student.entities.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByCnie(String cnie);

    Optional<Student> findByEmail(String email);

    boolean existsByCnie(String cnie);

    boolean existsByEmail(String email);
}
