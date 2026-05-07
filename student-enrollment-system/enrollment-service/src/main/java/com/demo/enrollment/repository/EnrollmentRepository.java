package com.demo.enrollment.repository;

import com.demo.enrollment.entities.Enrollment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    long countByCourseId(Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentIdOrderByEnrollmentDateDesc(Long studentId);
}
