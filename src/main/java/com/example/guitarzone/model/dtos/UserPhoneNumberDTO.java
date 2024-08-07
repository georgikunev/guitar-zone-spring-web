package com.example.guitarzone.model.dtos;

import jakarta.validation.constraints.Pattern;

public class UserPhoneNumberDTO {
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number is invalid")
    private String phoneNumber;

    public UserPhoneNumberDTO() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
