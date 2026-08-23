package com.demo.banking.client;

import com.demo.banking.wsdl.GetAccountBalanceRequest;
import com.demo.banking.wsdl.GetAccountBalanceResponse;
import com.demo.banking.wsdl.ProcessTransactionRequest;
import com.demo.banking.wsdl.ProcessTransactionResponse;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.demo.banking.dto.TransactionRequest;

import java.math.BigDecimal;

@Component
@SuppressWarnings("unused")
public class CoreBankingSoapClient {

    private final WebServiceTemplate webServiceTemplate;
    private final String coreBankingUrl = "http://localhost:8084/ws"; // In real env, point to actual legacy SOAP

    public CoreBankingSoapClient() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.demo.banking.wsdl");
        this.webServiceTemplate = new WebServiceTemplate(marshaller);
    }

    public Mono<GetAccountBalanceResponse> getAccountBalance(String accountId) {
        var request = new GetAccountBalanceRequest();
        request.setAccountId(accountId);

        return Mono.fromCallable(() -> {
            // Mocking the call since we don't have a real SOAP server running right now
            // return (GetAccountBalanceResponse) webServiceTemplate.marshalSendAndReceive(coreBankingUrl, request);
            
            GetAccountBalanceResponse mockResponse = new GetAccountBalanceResponse();
            mockResponse.setBalance(new BigDecimal("10500.00"));
            mockResponse.setCurrency("USD");
            return mockResponse;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ProcessTransactionResponse> processTransaction(TransactionRequest txRequest) {
        var request = new ProcessTransactionRequest();
        request.setTransactionId(txRequest.getTransactionId());
        request.setAccountId(txRequest.getAccountId());
        request.setAmount(txRequest.getAmount());
        request.setType(txRequest.getType());

        return Mono.fromCallable(() -> {
            // return (ProcessTransactionResponse) webServiceTemplate.marshalSendAndReceive(coreBankingUrl, request);
            ProcessTransactionResponse mockResponse = new ProcessTransactionResponse();
            mockResponse.setStatus("SUCCESS");
            mockResponse.setTransactionId(txRequest.getTransactionId());
            return mockResponse;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
