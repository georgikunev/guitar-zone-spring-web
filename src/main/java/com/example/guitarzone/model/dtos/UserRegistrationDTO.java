package com.example.guitarzone.model.dtos;

import jakarta.validation.constraints.*;

public class UserRegistrationDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 25, message = "First name should not be more than 25 characters")
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(max = 25, message = "Last name should not be more than 25 characters")
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should be at least 6 characters")
    private String password;
    @NotBlank(message = "Confirm password is required")
    @Size(min = 6, message = "Confirm password should be at least 6 characters")
    private String confirmPassword;

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
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
