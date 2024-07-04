package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.UserRegistrationDTO;
import com.example.guitarzone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("registerData")
    public UserRegistrationDTO registerDTO() {
        return new UserRegistrationDTO();
    }

    @GetMapping("/register")
    public String viewRegister() {

        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid UserRegistrationDTO userRegistrationDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors() || !userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("registerData", userRegistrationDTO);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.registerData", bindingResult);

            if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
            }

            return "redirect:/users/register";
        }
        boolean success = userService.registerUser(userRegistrationDTO);

        if (!success) {
            redirectAttributes.addFlashAttribute("registerData", userRegistrationDTO);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.registerData", bindingResult);
            bindingResult.rejectValue("email", "email.exists", "Email already exists");
            return "redirect:/users/register";
        }

        return "redirect:/users/login";
    }


}
