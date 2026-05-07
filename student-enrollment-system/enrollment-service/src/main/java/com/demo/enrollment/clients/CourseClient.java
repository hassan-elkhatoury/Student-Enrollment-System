package com.demo.enrollment.clients;

import com.demo.enrollment.dtos.CourseDTO;
import com.demo.enrollment.exceptions.RemoteServiceException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class CourseClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${services.course.base-url:http://course-service}")
    private String courseServiceUrl;

    public CourseDTO getCourseById(Long id) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(courseServiceUrl + "/api/courses/{id}", id)
                    .retrieve()
                    .bodyToMono(CourseDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            return null;
        } catch (WebClientRequestException ex) {
            throw new RemoteServiceException("Course Service is unavailable");
        }
    }

    public List<CourseDTO> getAllCourses() {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(courseServiceUrl + "/api/courses")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<CourseDTO>>() {
                    })
                    .block();
        } catch (WebClientRequestException ex) {
            throw new RemoteServiceException("Course Service is unavailable");
        }
    }
}
