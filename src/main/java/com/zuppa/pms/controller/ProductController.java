package com.zuppa.pms.controller;

import com.zuppa.pms.dto.ProductDto;
import com.zuppa.pms.entity.User;
import com.zuppa.pms.service.ProductService;
import com.zuppa.pms.service.CloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(productService.getAllProducts(currentUser));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody ProductDto productDto,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(productService.createProduct(productDto, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDto productDto,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        productService.deleteProduct(id, currentUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ProductDto> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(productService.updateProductImage(id, imageUrl, currentUser));
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }
}
