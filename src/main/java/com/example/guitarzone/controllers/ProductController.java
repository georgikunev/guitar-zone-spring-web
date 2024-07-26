package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.ShortProductInfoDTO;
import com.example.guitarzone.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/guitars")
public class ProductController {
    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //TODO Create loading logic for guitars so that loading all products when entering the could be prevented
    @GetMapping
    public String viewGuitar(Model model) {
        List<ShortProductInfoDTO> allProducts = productService.getAllProducts();
        model.addAttribute("products", allProducts);
        return "guitars";
    }

    @GetMapping("/guitar-details/{id}")
    public String viewGuitarDetails(@PathVariable("id") Long id, Model model) {
        model.addAttribute("productDetails", productService.getProductDetails(id));
        return "guitar-details";
    }


}
