package com.demo.banking.service;

import com.demo.banking.dto.TransactionRequest;
import com.demo.banking.wsdl.GetAccountBalanceResponse;
import com.demo.banking.wsdl.ProcessTransactionResponse;
import reactor.core.publisher.Mono;

public interface BankingService {
    Mono<GetAccountBalanceResponse> getAccountBalance(String accountId);
    Mono<ProcessTransactionResponse> processTransaction(TransactionRequest request);
}
