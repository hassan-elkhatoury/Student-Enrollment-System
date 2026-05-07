package com.demo.student.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StudentRequest {

    @NotBlank(message = "CNIE is required")
    @Size(max = 30, message = "CNIE must not exceed 30 characters")
    private String cnie;

    @NotBlank(message = "First name is required")
    @Size(max = 80, message = "First name must not exceed 80 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 80, message = "Last name must not exceed 80 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    public String getCnie() {
        return cnie;
    }

    public void setCnie(String cnie) {
        this.cnie = cnie;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
