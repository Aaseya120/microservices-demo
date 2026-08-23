package com.demo.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(RestClient.Builder restClientBuilder,
                         @Value("${eureka.client.service-url.defaultZone:http://localhost:8761/eureka/}") String eurekaUrl) {
        // Simple usage of RestClient. In a real environment with Eureka, we'd use LoadBalanced RestClient.Builder
        // For simplicity, assuming product-service is accessible or we use a hardcoded/lb URL.
        // Actually, since we have Eureka, we should use load balanced RestClient.Builder if Ribbon/LoadBalancer is present.
        this.restClient = restClientBuilder.baseUrl("http://product-service/api/products").build();
    }

    public ProductResponse getProduct(String productId) {
        return restClient.get()
                .uri("/{id}", productId)
                .retrieve()
                .body(ProductResponse.class);
    }

    public record ProductResponse(String id, String name, BigDecimal price, int stockQuantity) {}
}
