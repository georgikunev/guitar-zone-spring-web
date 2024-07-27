package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.ProductDetailsDTO;
import com.example.guitarzone.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String showManageProductsPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "manage-products";
    }

    @GetMapping("/edit")
    public String showEditProductPage(@RequestParam Long id, Model model) {
        model.addAttribute("product", productService.getProductDetails(id));
        return "edit-product";
    }
    //TODO Prevent reviews from getting deleted when updating products
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute ProductDetailsDTO productDetailsDTO) {
        productService.updateProduct(productDetailsDTO);
        return "redirect:/admin/products";
    }
    //TODO Deal with orders interference
    @PostMapping("/remove")
    public String removeProduct(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.removeProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product removed successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/new")
    public String showCreateProductPage(Model model) {
        model.addAttribute("product", new ProductDetailsDTO());
        return "create-product";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute ProductDetailsDTO productDetailsDTO) {
        productService.saveProduct(productDetailsDTO);
        return "redirect:/admin/products";
    }
}
