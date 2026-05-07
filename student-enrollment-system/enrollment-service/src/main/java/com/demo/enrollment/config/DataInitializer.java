package com.demo.enrollment.config;

import com.demo.enrollment.entities.Enrollment;
import com.demo.enrollment.repository.EnrollmentRepository;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner loadEnrollments(EnrollmentRepository enrollmentRepository) {
        return args -> {
            if (enrollmentRepository.count() > 0) {
                return;
            }

            enrollmentRepository.save(new Enrollment(1L, 1L, LocalDateTime.now().minusHours(2)));
            enrollmentRepository.save(new Enrollment(1L, 2L, LocalDateTime.now().minusHours(30)));
            enrollmentRepository.save(new Enrollment(2L, 1L, LocalDateTime.now().minusHours(4)));
        };
    }
}
