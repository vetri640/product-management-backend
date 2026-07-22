package com.zuppa.pms.controller;

import com.zuppa.pms.dto.ProductDto;
import com.zuppa.pms.entity.User;
import com.zuppa.pms.service.ProductService;
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
            // Create uploads directory if it doesn't exist
            java.io.File uploadDir = new java.io.File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String fileName = java.util.UUID.randomUUID().toString() + extension;
            
            // Save file
            java.nio.file.Path filePath = java.nio.file.Paths.get("uploads", fileName);
            java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // Construct URL
            // Ensure the frontend accesses it via localhost:8081/uploads/filename
            String imageUrl = "http://localhost:8081/uploads/" + fileName;
            
            return ResponseEntity.ok(productService.updateProductImage(id, imageUrl, currentUser));
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
