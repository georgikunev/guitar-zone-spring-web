package com.example.guitarzone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String viewHome() {

        return "index";
    }

    @GetMapping("/about")
    public String viewAbout() {

        return "about";
    }

    @GetMapping("/contact")
    public String viewContact() {

        return "contact";
    }

    @GetMapping("/users/login")
    public String viewLogin() {

        return "login";
    }

    @GetMapping("/checkout")
    public String viewCheckout() {

        return "checkout";
    }


}
