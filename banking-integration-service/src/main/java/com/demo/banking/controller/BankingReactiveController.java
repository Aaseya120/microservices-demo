package com.demo.banking.controller;

import com.demo.banking.client.CoreBankingSoapClient;
import com.demo.banking.wsdl.GetAccountBalanceResponse;
import com.demo.banking.wsdl.ProcessTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/banking")
@RequiredArgsConstructor
@Slf4j
public class BankingReactiveController {

    private final CoreBankingSoapClient soapClient;

    @GetMapping("/balance/{accountId}")
    public Mono<GetAccountBalanceResponse> getBalance(@PathVariable String accountId) {
        log.info("Reactive request for balance on account: {}", accountId);
        return soapClient.getAccountBalance(accountId);
    }

    @PostMapping("/transaction")
    public Mono<ProcessTransactionResponse> processTransaction(@RequestParam String transactionId,
                                                               @RequestParam String accountId,
                                                               @RequestParam BigDecimal amount,
                                                               @RequestParam String type) {
        log.info("Reactive request for transaction {} on account: {}", transactionId, accountId);
        return soapClient.processTransaction(transactionId, accountId, amount, type);
    }
}
