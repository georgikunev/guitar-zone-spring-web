package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.ShortProductInfoDTO;
import com.example.guitarzone.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/guitars")
    public String viewGuitar(Model model) {
        List<ShortProductInfoDTO> allProducts = productService.getAllProducts();
        model.addAttribute("products", allProducts);
        return "guitars";
    }


}
