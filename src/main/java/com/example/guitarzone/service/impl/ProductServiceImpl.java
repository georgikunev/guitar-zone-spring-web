package com.example.guitarzone.service.impl;

import com.example.guitarzone.model.dtos.ProductDetailsDTO;
import com.example.guitarzone.model.dtos.ShortProductInfoDTO;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.repositories.ProductRepository;
import com.example.guitarzone.service.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public List<ShortProductInfoDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        logger.debug("Fetched products: {}", products);
        return products.stream().map(this::mapToShortInfo).toList();
    }

    @Override
    public ProductDetailsDTO getProductDetails(Long id) {
        return productRepository.findById(id).map(this::mapToDetailsInfo).orElseThrow();
    }

    private ShortProductInfoDTO mapToShortInfo(Product product) {

        return modelMapper.map(product, ShortProductInfoDTO.class);
    }
    private ProductDetailsDTO mapToDetailsInfo(Product product) {

        return modelMapper.map(product, ProductDetailsDTO.class);
    }
}
