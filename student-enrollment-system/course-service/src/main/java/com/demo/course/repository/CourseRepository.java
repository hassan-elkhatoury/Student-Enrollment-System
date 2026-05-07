package com.demo.course.repository;

import com.demo.course.entities.Course;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByTitle(String title);

    boolean existsByTitle(String title);
}
