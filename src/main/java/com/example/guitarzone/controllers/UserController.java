package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.UserAccountInfoDTO;
import com.example.guitarzone.model.dtos.UserRegistrationDTO;
import com.example.guitarzone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/login")
    public String viewLogin() {

        return "login";
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
    @GetMapping("/account")
    public String showAccountPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        UserAccountInfoDTO userAccountInfoDTO = userService.getAccountDetails(email);
        model.addAttribute("userAccountInfoDTO", userAccountInfoDTO);
        return "user-account";
    }

    //TODO Separate names and phone number input fields
    @PostMapping("/account")
    public String updateAccount(@ModelAttribute("userAccountDTO") @Valid UserAccountInfoDTO userAccountInfoDTO,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Handling validation errors
            model.addAttribute("userAccountInfoDTO", userAccountInfoDTO); // Add form back to the model to retain form data
            return "user-account"; // Return directly to the view, not a redirect
        }
        String email = userDetails.getUsername();
        userService.updateUserAccount(email, userAccountInfoDTO);
        redirectAttributes.addFlashAttribute("success", "Your account has been updated successfully.");
        return "redirect:/users/account"; // Redirect to avoid duplicate submissions
    }

}
