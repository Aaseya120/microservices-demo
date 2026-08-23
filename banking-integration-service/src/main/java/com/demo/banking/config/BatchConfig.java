package com.demo.banking.config;

import com.demo.banking.client.CoreBankingSoapClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CoreBankingSoapClient soapClient;

    @Bean
    public Job reconciliationJob() {
        return new JobBuilder("reconciliationJob", jobRepository)
                .start(reconciliationStep())
                .build();
    }

    @Bean
    public Step reconciliationStep() {
        return new StepBuilder("reconciliationStep", jobRepository)
                .tasklet(reconciliationTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet reconciliationTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Starting End-of-Day Reconciliation Batch Job...");

            // In a real scenario, we'd read from a database or file in chunks.
            // For demo purposes, we process a single mock transaction via our SoapClient.
            String mockTransactionId = UUID.randomUUID().toString();
            
            log.info("Processing transaction {} via SOAP...", mockTransactionId);
            
            // Blocking wait here because Spring Batch Tasklet is synchronous.
            var response = soapClient.processTransaction(mockTransactionId, "ACC123", new BigDecimal("100.00"), "EOD_SETTLEMENT").block();

            log.info("Reconciliation successful for {}. Status: {}", mockTransactionId, response.getStatus());

            return RepeatStatus.FINISHED;
        };
    }
}
