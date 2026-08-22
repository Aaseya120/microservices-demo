package com.demo.product.saga;

import com.demo.product.entity.Product;
import com.demo.product.repository.ProductRepository;
import com.demo.product.saga.command.ReserveInventoryCommand;
import com.demo.product.saga.reply.InventoryReply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCommandHandler {

    private final ProductRepository productRepository;

    @KafkaListener(topics = "product-commands", groupId = "product-group")
    @SendTo("product-replies")
    @Transactional
    public InventoryReply handleReserveInventory(ReserveInventoryCommand command) {
        log.info("Received ReserveInventoryCommand: {}", command);

        Product product = productRepository.findById(command.productId()).orElse(null);
        if (product == null) {
            return new InventoryReply(command.orderId(), "FAILED", "Product not found");
        }

        if (product.getStockQuantity() >= command.quantity()) {
            product.setStockQuantity(product.getStockQuantity() - command.quantity());
            productRepository.save(product);
            return new InventoryReply(command.orderId(), "RESERVED", "Stock updated");
        } else {
            return new InventoryReply(command.orderId(), "FAILED", "Insufficient stock. Available: " + product.getStockQuantity());
        }
    }
}
