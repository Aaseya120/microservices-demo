package com.demo.product.service;

import com.demo.product.dto.Dtos.ProductRequest;
import com.demo.product.entity.Product;
import com.demo.product.exception.ProductExceptions;
import com.demo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(ProductRequest req) {
        Product product = Product.builder()
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .stockQuantity(req.stockQuantity())
                .build();
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public Product getProduct(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductExceptions.ProductNotFoundException("Product not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional
    @CachePut(value = "products", key = "#id")
    public Product updateProduct(String id, ProductRequest req) {
        Product product = getProduct(id);
        product.setName(req.name());
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setStockQuantity(req.stockQuantity());
        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductExceptions.ProductNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }
}
