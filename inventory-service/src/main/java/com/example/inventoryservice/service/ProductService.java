package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.CreateProductRequest;
import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void seedData() {
        if (productRepository.count() == 0) {
            productRepository.save(new Product("SKU-001", "Wireless Mouse", 50));
            productRepository.save(new Product("SKU-002", "Mechanical Keyboard", 30));
            productRepository.save(new Product("SKU-003", "USB-C Hub", 100));
        }
    }

    public Product createProduct(CreateProductRequest request) {
        Product product = new Product(request.getProductId(), request.getName(), request.getStockQuantity());
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProduct(String productId) {
        return productRepository.findById(productId);
    }
}
