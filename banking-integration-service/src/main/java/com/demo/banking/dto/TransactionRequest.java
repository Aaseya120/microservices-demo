package com.demo.banking.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String transactionId;
    private String accountId;
    private BigDecimal amount;
    private String type;
}
