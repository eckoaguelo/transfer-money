package com.transaction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "transaction")
public class TransactionConfig {

    private Map<String, BigDecimal> rates;
    private BigDecimal usdToAud;
    private BigDecimal audToUsd;
    private BigDecimal transactionFeePercentage;

}