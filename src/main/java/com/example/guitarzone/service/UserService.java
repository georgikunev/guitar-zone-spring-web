package com.example.guitarzone.service;

import com.example.guitarzone.model.dtos.UserAccountInfoDTO;
import com.example.guitarzone.model.dtos.UserRegistrationDTO;
import com.example.guitarzone.model.entities.User;

import java.util.List;


public interface UserService {

    boolean registerUser(UserRegistrationDTO userRegistrationDTO);
    User findByEmail(String email);
    void updateUserAccount(String email, UserAccountInfoDTO userAccountInfoDTO);
    void updateUserName(String email, String firstName, String lastName);
    void updateUserPhone(String email, String phoneNumber);
    UserAccountInfoDTO getAccountDetails(String email);
    List<User> getAllUsers();
}
