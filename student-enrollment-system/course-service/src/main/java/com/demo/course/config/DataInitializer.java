package com.demo.course.config;

import com.demo.course.entities.Course;
import com.demo.course.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner loadCourses(CourseRepository courseRepository) {
        return args -> {
            if (courseRepository.count() > 0) {
                return;
            }

            courseRepository.save(new Course("Spring Framework", "Controllers, dependency injection, services and repositories.", 4));
            courseRepository.save(new Course("Data Mining", "Data preparation, classification and knowledge extraction.", 3));
            courseRepository.save(new Course("Microservices Architecture", "API Gateway, service discovery and service-to-service communication.", 4));
            courseRepository.save(new Course("Advanced Java", "Java OOP, collections, streams and enterprise patterns.", 3));
        };
    }
}
