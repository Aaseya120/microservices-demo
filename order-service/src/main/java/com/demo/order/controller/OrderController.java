package com.demo.order.controller;

import com.demo.order.dto.Dtos.OrderRequest;
import com.demo.order.entity.Order;
import com.demo.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
@Tag(name = "Order API", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Value("${app.business.discount-rate:0.0}")
    private Double discountRate;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new order")
    public Order createOrder(@RequestBody OrderRequest request, @AuthenticationPrincipal Jwt jwt) {
        log.info("Creating order for product {} by user {} with discount {}", request.productId(), request.userId(), discountRate);
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get order by ID")
    public Order getOrder(@PathVariable String id) {
        log.info("Fetching order with ID: {}", id);
        return orderService.getOrder(id);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == #jwt.claimAsString('preferred_username')")
    @Operation(summary = "Get orders by user ID with pagination")
    public Page<Order> getOrdersByUser(@PathVariable String userId, Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        log.info("Fetching orders for user: {}", userId);
        return orderService.getByUser(userId, pageable);
    }
}
