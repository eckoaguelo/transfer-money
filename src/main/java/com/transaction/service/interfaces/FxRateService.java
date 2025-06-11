package com.transaction.service.interfaces;

import java.math.BigDecimal;

public interface FxRateService {
    BigDecimal getRate(String senderCurrency, String receiverCurrency);
}
