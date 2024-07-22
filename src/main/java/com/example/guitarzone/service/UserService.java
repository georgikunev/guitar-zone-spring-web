package com.example.guitarzone.service;

import com.example.guitarzone.model.dtos.UserAccountInfoDTO;
import com.example.guitarzone.model.dtos.UserRegistrationDTO;
import com.example.guitarzone.model.entities.User;

public interface UserService {

    boolean registerUser(UserRegistrationDTO userRegistrationDTO);
    User findByEmail(String email);
    void updateUserAccount(String email, UserAccountInfoDTO userAccountInfoDTO);
    UserAccountInfoDTO getAccountDetails(String email);


}
