package com.transaction.service.interfaces;

import com.transaction.request.TransferMoneyRequest;

public interface TransactionService {
    void transfer(TransferMoneyRequest request);
}
