package com.zuppa.pms.service;

import com.zuppa.pms.dto.ProductDto;
import com.zuppa.pms.entity.Product;
import com.zuppa.pms.entity.Role;
import com.zuppa.pms.entity.User;
import com.zuppa.pms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDto> getAllProducts(User currentUser) {
        List<Product> products;
        if (currentUser.getRole() == Role.ADMIN) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByUserId(currentUser.getId());
        }
        return products.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public ProductDto createProduct(ProductDto productDto, User currentUser) {
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .imageUrl(productDto.getImageUrl())
                .stock(productDto.getStock() != null ? productDto.getStock() : 0)
                .status(productDto.getStatus() != null ? productDto.getStatus() : "ACTIVE")
                .category(productDto.getCategory())
                .modelNumber(productDto.getModelNumber())
                .user(currentUser)
                .build();
        Product savedProduct = productRepository.save(product);
        return mapToDto(savedProduct);
    }

    public ProductDto updateProduct(Long id, ProductDto productDto, User currentUser) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (currentUser.getRole() != Role.ADMIN && !product.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to update this product");
        }

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        
        if (productDto.getStock() != null) {
            product.setStock(productDto.getStock());
        }
        if (productDto.getStatus() != null) {
            product.setStatus(productDto.getStatus());
        }
        if (productDto.getCategory() != null) {
            product.setCategory(productDto.getCategory());
        }
        if (productDto.getModelNumber() != null) {
            product.setModelNumber(productDto.getModelNumber());
        }
        
        if (productDto.getImageUrl() != null) {
            product.setImageUrl(productDto.getImageUrl());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToDto(updatedProduct);
    }

    public ProductDto updateProductImage(Long id, String imageUrl, User currentUser) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (currentUser.getRole() != Role.ADMIN && !product.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to update this product");
        }

        product.setImageUrl(imageUrl);
        Product updatedProduct = productRepository.save(product);
        return mapToDto(updatedProduct);
    }

    public void deleteProduct(Long id, User currentUser) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (currentUser.getRole() != Role.ADMIN && !product.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to delete this product");
        }

        productRepository.delete(product);
    }

    private ProductDto mapToDto(Product product) {
        String ownerName = product.getUser().getName();
        if (ownerName == null || ownerName.trim().isEmpty()) {
            ownerName = "User";
        }
        String ownerStr = ownerName + " (" + product.getUser().getRole().name() + ")";

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .stock(product.getStock())
                .status(product.getStatus())
                .category(product.getCategory())
                .modelNumber(product.getModelNumber())
                .userId(product.getUser().getId())
                .owner(ownerStr)
                .build();
    }
}
