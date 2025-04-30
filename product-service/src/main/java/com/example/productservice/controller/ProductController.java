package com.example.productservice.controller;

import com.example.productservice.dto.ProductDto;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/available")
    public List<ProductDto> getAvailableProducts() {
        return productService.getAvailableProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.status(201).body(productService.createProduct(productDto));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<ProductDto> uploadProductImage(
            @PathVariable String id,
            @RequestPart("file") MultipartFile file) {

        return ResponseEntity.ok(productService.uploadProductImage(id, file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String id, @RequestBody ProductDto ProductDto) {
        return ResponseEntity.ok(productService.updateProduct(id, ProductDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}/availability")
    public ResponseEntity<Void> setAvailability(@PathVariable String productId, @RequestParam boolean available) {
        productService.setAvailability(productId, available);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductDto>> getProductsByIds(@RequestBody List<String> ids) {
        return ResponseEntity.ok(productService.getProductsByIds(ids));
    }


}

