package com.example.guitarzone.service;

import com.example.guitarzone.model.dtos.ProductDetailsDTO;
import com.example.guitarzone.model.dtos.ShortProductInfoDTO;

import java.util.List;

public interface ProductService {

    List<ShortProductInfoDTO> getAllProducts();
    ProductDetailsDTO getProductDetails(Long id);
    void updateProduct(ProductDetailsDTO productDetailsDTO);
    void removeProduct(Long id);
    void saveProduct(ProductDetailsDTO productDetailsDTO);

}
