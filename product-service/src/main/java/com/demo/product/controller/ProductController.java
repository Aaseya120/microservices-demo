package com.demo.product.controller;

import com.demo.product.dto.Dtos.ApiResponse;
import com.demo.product.dto.Dtos.ProductRequest;
import com.demo.product.entity.Product;
import com.demo.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product API", description = "Operations related to products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductRequest req) {
        log.info("Creating product with name: {}", req.name());
        Product product = productService.createProduct(req);
        return ResponseEntity.status(201).body(new ApiResponse<>(product, "Product created", true));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable String id) {
        log.info("Fetching product with ID: {}", id);
        return ResponseEntity.ok(new ApiResponse<>(productService.getProduct(id), "OK", true));
    }

    @GetMapping
    @Operation(summary = "Get all products (paginated)")
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(Pageable pageable) {
        log.info("Fetching all products with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(new ApiResponse<>(productService.getAllProducts(pageable), "OK", true));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product by ID")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable String id, @Valid @RequestBody ProductRequest req) {
        log.info("Updating product with ID: {}", id);
        return ResponseEntity.ok(new ApiResponse<>(productService.updateProduct(id, req), "Product updated", true));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by ID")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        log.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(null, "Product deleted", true));
    }
}
