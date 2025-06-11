package com.transaction.service;

import com.transaction.config.TransactionConfig;
import com.transaction.entity.Account;
import com.transaction.exception.TSBadRequestException;
import com.transaction.repository.AccountRepository;
import com.transaction.request.TransferMoneyRequest;
import com.transaction.service.interfaces.AuditService;
import com.transaction.service.interfaces.FxRateService;
import com.transaction.service.interfaces.TransactionService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.transaction.constant.TransactionConstant.*;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionConfig config;
    private final FxRateService fxRateService;
    private final AuditService auditService;

    public TransactionServiceImpl(AccountRepository accountRepository, TransactionConfig config,
                                  FxRateService fxRateService, AuditService auditService) {
        this.accountRepository = accountRepository;
        this.config = config;
        this.fxRateService = fxRateService;
        this.auditService = auditService;
    }

    @Transactional
    public void transfer(TransferMoneyRequest request){
        BigDecimal originalAmount = request.getAmount();
        if(originalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new TSBadRequestException("Negative amount not allowed", NEGATIVE_AMT_ERR);
        }

        // check if accounts exist
        Account sender = accountRepository.findByIdForUpdate(request.getSenderAccountId()).orElseThrow(() -> {
                    log.info("Sender account not found: {}", request.getSenderAccountId());
                    return new TSBadRequestException("Sender account not found", NOT_FOUND_ERR);
                });
        Account receiver = accountRepository.findByIdForUpdate(request.getReceiverAccountId()).orElseThrow(() -> {
                    log.info("Receiver account not found: {}", request.getSenderAccountId());
                    return new TSBadRequestException("Receiver account not found", NOT_FOUND_ERR);
                });

        // throw error if requested currency does not match sender's currency
        if (!request.getCurrency().equals(sender.getCurrency())) {
            log.info("Currency mismatch: {} {}", request.getCurrency(), sender.getCurrency());
            throw new TSBadRequestException("Currency mismatch", MISMATCH_ERR);
        }

        BigDecimal convertedAmount = originalAmount;
        // check if currencies match
        if (!sender.getCurrency().equals(receiver.getCurrency())) {
            //get fx rate conversion
            BigDecimal fxRate = fxRateService.getRate(sender.getCurrency(), receiver.getCurrency());
            convertedAmount = originalAmount.multiply(fxRate);
            log.info("Converted amount from {} to {}: {}", sender.getCurrency(), receiver.getCurrency(), convertedAmount);
        }

        // add transaction fee
        BigDecimal fee = originalAmount.multiply(config.getTransactionFeePercentage());
        BigDecimal totalOriginalAmount = originalAmount.add(fee);

        if (sender.getBalance().compareTo(totalOriginalAmount) < 0) {
            log.info("Insufficient balance: {}. Amount to transfer: {}", sender.getBalance(), totalOriginalAmount);
            throw new TSBadRequestException("Insufficient balance", BAL_ERR);
        }

        sender.setBalance(sender.getBalance().subtract(totalOriginalAmount));
        receiver.setBalance(receiver.getBalance().add(convertedAmount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        // save transaction details
        auditService.logTransaction(sender.getId(), TRANSFER_MONEY, "Transferred " +
                originalAmount + " from account ID " + sender.getId() + " to " +
                receiver.getId() + " with " + fee + " fee");

        log.info("Deducted amount from sender's account with fee: {}", totalOriginalAmount);
        log.info("Transferred amount to receiver: {}", convertedAmount);
    }
}

