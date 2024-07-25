package com.example.guitarzone.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserAccountInfoDTO {
    @NotBlank(message = "First name is required")
    @Size(max = 25, message = "First name should not be more than 25 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 25, message = "Last name should not be more than 25 characters")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number is invalid")
    private String phoneNumber;

    public UserAccountInfoDTO() {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
