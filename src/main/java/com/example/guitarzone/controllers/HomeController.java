package com.example.guitarzone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    private String viewHome(){

        return "index";
    }

    @GetMapping("/about")
    private String viewAbout(){

        return "about";
    }

    @GetMapping("/contact")
    private String viewContact(){

        return "contact";
    }
/* @GetMapping("/guitars")
    private String viewGuitar(){

        return "guitars";
    }

    @GetMapping("/cart")
    private String viewCart(){

        return "cart";
    }
    @GetMapping("/login")
    private String viewLogin(){

        return "login";
    }
    @GetMapping("/register")
    private String viewRegister(){

        return "register";
    }

    @GetMapping("/guitar-details")
    private String viewGuitarDetails(){

        return "guitar-details";
    }

    @GetMapping("/checkout")
    private String viewCheckout(){

        return "checkout";
    }
* */

}
