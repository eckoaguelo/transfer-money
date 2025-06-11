package com.transaction.service;

import com.transaction.config.TransactionConfig;
import com.transaction.exception.TSBadRequestException;
import com.transaction.service.interfaces.FxRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.transaction.constant.TransactionConstant.MISMATCH_ERR;

@Slf4j
@Service
public class FxRateServiceImpl implements FxRateService {
    private final TransactionConfig transactionConfig;

    public FxRateServiceImpl(TransactionConfig transactionConfig) {
        this.transactionConfig = transactionConfig;
    }

    public BigDecimal getRate(String senderCurrency, String receiverCurrency) {
        String key = senderCurrency + "-" + receiverCurrency;
        BigDecimal rate = transactionConfig.getRates().get(key);

        log.info("rate: {} for key: {}", rate, key);
        if(rate == null) {
            throw new TSBadRequestException("Currency mismatch", MISMATCH_ERR);
        }

        return rate;
    }
}
