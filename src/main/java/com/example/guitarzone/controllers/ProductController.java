package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.ProductDetailsDTO;
import com.example.guitarzone.model.dtos.ShortProductInfoDTO;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.service.ProductService;
import com.example.guitarzone.service.WishlistService;
import com.example.guitarzone.service.impl.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/guitars")
public class ProductController {
    private final ProductService productService;
    private final WishlistService wishlistService;


    public ProductController(ProductService productService, WishlistService wishlistService, ModelMapper modelMapper) {
        this.productService = productService;
        this.wishlistService = wishlistService;
    }

    //TODO Create loading logic for guitars so that loading all products when entering the page could be prevented
    @GetMapping
    public String viewGuitar(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<ShortProductInfoDTO> allProducts = productService.getAllProducts();

        if (userDetails != null) {
            Long userId = userDetails.getId();
            List<Product> wishlistItems = wishlistService.getWishlistItems(userId);
            Set<Long> wishlistProductIds = wishlistItems.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());

            allProducts.forEach(product -> product.setInWishlist(wishlistProductIds.contains(product.getId())));
        }

        model.addAttribute("products", allProducts);
        model.addAttribute("userDetails", userDetails);

        return "guitars";

    }

    @GetMapping("/guitar-details/{id}")
    public String viewGuitarDetails(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProductDetailsDTO productDetails = productService.getProductDetails(id);

        if (userDetails != null) {
            Long userId = userDetails.getId();
            List<Product> wishlistItems = wishlistService.getWishlistItems(userId);
            productDetails.setInWishlist(wishlistItems.stream().anyMatch(product -> product.getId().equals(id)));
        }

        model.addAttribute("productDetails", productDetails);
        model.addAttribute("userDetails", userDetails);

        return "guitar-details";
    }


}
