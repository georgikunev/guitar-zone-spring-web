package com.example.guitarzone.service;

import com.example.guitarzone.model.dtos.UserRegistrationDTO;

public interface UserService {

    boolean registerUser(UserRegistrationDTO userRegistrationDTO);

}
