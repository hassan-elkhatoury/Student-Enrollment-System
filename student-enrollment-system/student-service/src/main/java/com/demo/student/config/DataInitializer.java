package com.demo.student.config;

import com.demo.student.entities.Student;
import com.demo.student.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner loadStudents(StudentRepository studentRepository) {
        return args -> {
            if (studentRepository.count() > 0) {
                return;
            }

            studentRepository.save(new Student("CD2387", "Yassine", "El Amrani", "yassine.elamrani@uae.ac.ma"));
            studentRepository.save(new Student("AB1045", "Salma", "Bennani", "salma.bennani@uae.ac.ma"));
            studentRepository.save(new Student("JK7721", "Imane", "Zahraoui", "imane.zahraoui@uae.ac.ma"));
            studentRepository.save(new Student("EF5520", "Omar", "Rami", "omar.rami@uae.ac.ma"));
        };
    }
}
