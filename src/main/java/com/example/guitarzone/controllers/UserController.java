package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.UserAccountInfoDTO;
import com.example.guitarzone.model.dtos.UserPhoneNumberDTO;
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

import java.util.function.Consumer;


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
    @ModelAttribute
    public void addAttributes(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            UserAccountInfoDTO userAccountInfoDTO = userService.getAccountDetails(email);
            UserPhoneNumberDTO userPhoneNumberDTO = new UserPhoneNumberDTO();
            userPhoneNumberDTO.setPhoneNumber(userAccountInfoDTO.getPhoneNumber());
            model.addAttribute("userAccountInfoDTO", userAccountInfoDTO);
            model.addAttribute("userPhoneNumberDTO", userPhoneNumberDTO);
        }
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
        UserPhoneNumberDTO userPhoneNumberDTO = new UserPhoneNumberDTO();
        userPhoneNumberDTO.setPhoneNumber(userAccountInfoDTO.getPhoneNumber());
        model.addAttribute("userAccountInfoDTO", userAccountInfoDTO);
        model.addAttribute("userPhoneNumberDTO", userPhoneNumberDTO);
        return "user-account";
    }

    @PostMapping("/account/update-name")
    public String updateName(@ModelAttribute("userAccountInfoDTO") @Valid UserAccountInfoDTO userAccountInfoDTO,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        return updateUserAccountField(
                userDetails.getUsername(),
                bindingResult,
                model,
                redirectAttributes,
                "userAccountInfoDTO",
                "Your name has been updated successfully.",
                (email) -> userService.updateUserName(email, userAccountInfoDTO.getFirstName(), userAccountInfoDTO.getLastName())
        );
    }

    @PostMapping("/account/update-phone")
    public String updatePhone(@ModelAttribute("userPhoneNumberDTO") @Valid UserPhoneNumberDTO userPhoneNumberDTO,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        return updateUserAccountField(
                userDetails.getUsername(),
                bindingResult,
                model,
                redirectAttributes,
                "userPhoneNumberDTO",
                "Your phone number has been updated successfully.",
                (email) -> userService.updateUserPhone(email, userPhoneNumberDTO.getPhoneNumber())
        );
    }

    private String updateUserAccountField(String email,
                                          BindingResult bindingResult,
                                          Model model,
                                          RedirectAttributes redirectAttributes,
                                          String attributeName,
                                          String successMessage,
                                          Consumer<String> updateFunction) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(attributeName, bindingResult.getTarget());
            return "user-account";
        }
        updateFunction.accept(email);
        redirectAttributes.addFlashAttribute("success", successMessage);
        return "redirect:/users/account";
    }
}
