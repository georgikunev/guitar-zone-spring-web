package com.example.guitarzone.service;

import com.example.guitarzone.model.dtos.ShortProductInfoDTO;

import java.util.List;

public interface ProductService {

    List<ShortProductInfoDTO> getAllProducts();

}
