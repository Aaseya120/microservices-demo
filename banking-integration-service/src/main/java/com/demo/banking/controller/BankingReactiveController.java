package com.demo.banking.controller;

import com.demo.banking.dto.TransactionRequest;
import com.demo.banking.service.BankingService;
import com.demo.banking.wsdl.GetAccountBalanceResponse;
import com.demo.banking.wsdl.ProcessTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/banking")
@RequiredArgsConstructor
@Slf4j
public class BankingReactiveController {

    private final BankingService bankingService;

    @GetMapping("/balance/{accountId}")
    public Mono<GetAccountBalanceResponse> getBalance(@PathVariable String accountId) {
        log.info("Reactive request for balance on account: {}", accountId);
        return bankingService.getAccountBalance(accountId);
    }

    @PostMapping("/transaction")
    public Mono<ProcessTransactionResponse> processTransaction(@RequestBody TransactionRequest request) {
        log.info("Reactive request for transaction {} on account: {}", request.getTransactionId(), request.getAccountId());
        return bankingService.processTransaction(request);
    }
}
