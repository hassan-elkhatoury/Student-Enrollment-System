package com.demo.enrollment.clients;

import com.demo.enrollment.dtos.StudentDTO;
import com.demo.enrollment.exceptions.RemoteServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class StudentClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${services.student.base-url:http://student-service}")
    private String studentServiceUrl;

    public StudentDTO getStudentByCnie(String cnie) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(studentServiceUrl + "/api/students/by-cnie/{cnie}", cnie)
                    .retrieve()
                    .bodyToMono(StudentDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            return null;
        } catch (WebClientRequestException ex) {
            throw new RemoteServiceException("Student Service is unavailable");
        }
    }
}
