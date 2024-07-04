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

    @GetMapping("/cart")
    public String viewCart() {

        return "cart";
    }

    @GetMapping("/users/login")
    public String viewLogin() {

        return "login";
    }


    @GetMapping("/guitar-details")
    public String viewGuitarDetails() {

        return "guitar-details";
    }

    @GetMapping("/checkout")
    public String viewCheckout() {

        return "checkout";
    }


}
