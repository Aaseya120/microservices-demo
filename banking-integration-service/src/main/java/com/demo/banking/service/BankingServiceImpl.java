package com.demo.banking.service;

import com.demo.banking.client.CoreBankingSoapClient;
import com.demo.banking.dto.TransactionRequest;
import com.demo.banking.wsdl.GetAccountBalanceResponse;
import com.demo.banking.wsdl.ProcessTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankingServiceImpl implements BankingService {

    private final CoreBankingSoapClient soapClient;

    @Override
    public Mono<GetAccountBalanceResponse> getAccountBalance(String accountId) {
        log.info("Delegating getAccountBalance to CoreBankingSoapClient for account: {}", accountId);
        return soapClient.getAccountBalance(accountId);
    }

    @Override
    public Mono<ProcessTransactionResponse> processTransaction(TransactionRequest request) {
        log.info("Delegating processTransaction to CoreBankingSoapClient for transaction {} on account: {}", request.getTransactionId(), request.getAccountId());
        return soapClient.processTransaction(request);
    }
}
