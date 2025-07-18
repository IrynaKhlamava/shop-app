package com.example.productservice.service;

import com.example.productservice.dto.CartItemDto;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.exception.FileUploadException;
import com.example.productservice.exception.NotEnoughStockException;
import com.example.productservice.exception.ResourceNotFoundException;
import com.example.productservice.mapper.ProductMapper;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${app.upload-product-image-path}")
    private String uploadProductImagePath;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final MongoTemplate mongoTemplate;

    public List<ProductDto> getAllProducts() {
        logger.info("Get all products");
        return productRepository.findAll().stream().map(productMapper::toDto).toList();
    }

    public ProductDto getProductById(String id) {
        logger.info("Get product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product", id));
        return productMapper.toDto(product);
    }

    public List<ProductDto> getProductsByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Product ID list must not be empty");
        }

        List<Product> products = productRepository.findAllById(ids);

        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Products", ids.toString());
        }

        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        logger.info("Creating new product: {}", productDto.getName());
        return productMapper.toDto(productRepository.save(product));
    }

    public ProductDto uploadProductImage(String productId, MultipartFile file) {
        logger.info("Uploading image for product ID={}", productId);

        Product product = getProductByIdOrThrow(productId);

        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            product.setImage(uploadProductImagePath + filename);
            productRepository.save(product);

            logger.info("Uploaded image for product {}: {}", productId, filename);
            return productMapper.toDto(product);

        } catch (IOException e) {
            logger.error("File upload failed for product ID={}: {}", productId, e.getMessage());
            throw new FileUploadException();
        }
    }

    private Product getProductByIdOrThrow(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    }

    public ProductDto updateProduct(String id, ProductDto ProductDto) {
        logger.info("Updating product with id: {}", id);
        Product product = productMapper.toEntity(ProductDto);
        product.setId(id);
        return productMapper.toDto(productRepository.save(product));
    }

    public void deleteProduct(String id) {
        logger.warn("Deleting product with id: {}", id);
        productRepository.deleteById(id);
    }

    public void updateStockAfterOrder(List<CartItemDto> items) {
        for (CartItemDto item : items) {
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            Query query = new Query(Criteria.where("_id").is(productId).and("amountLeft").gte(quantity));
            Update update = new Update().inc("amountLeft", -quantity);

            Product updatedProduct = mongoTemplate.findAndModify(query, update,
                    FindAndModifyOptions.options().returnNew(false), Product.class
            );

            if (updatedProduct == null) {
                throw new NotEnoughStockException();
            }

            logger.info("Stock updated successfully for product {}. Deducted {}", productId, quantity);
        }
    }

    public void setAvailability(String productId, boolean available) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        product.setAvailable(available);
        productRepository.save(product);
    }

    public List<ProductDto> getAvailableProducts() {
        List<Product> products = productRepository.findByAvailableTrue();
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public void increaseStock(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        int previousStock = product.getAmountLeft();
        product.setAmountLeft(previousStock + quantity);
        productRepository.save(product);

        logger.info("Stock updated for product [{}]: {} -> {}", productId, previousStock, product.getAmountLeft());
    }

}
