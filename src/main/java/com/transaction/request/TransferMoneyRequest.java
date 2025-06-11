package com.transaction.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferMoneyRequest {
    public Long senderAccountId;
    public Long receiverAccountId;
    public BigDecimal amount;
}
