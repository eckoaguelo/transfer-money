import com.transaction.config.TransactionConfig;
import com.transaction.entity.Account;
import com.transaction.exception.TSBadRequestException;
import com.transaction.repository.AccountRepository;
import com.transaction.request.TransferMoneyRequest;
import com.transaction.service.TransactionServiceImpl;
import com.transaction.service.interfaces.AuditService;
import com.transaction.service.interfaces.FxRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionConfig config;
    @Mock
    private FxRateService fxRateService;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    public void testTransferSuccessfulSameCurrency() {
        Account sender = new Account();
        sender.setId(1L);
        sender.setCurrency("USD");
        sender.setBalance(new BigDecimal("100.00"));

        Account receiver = new Account();
        receiver.setId(2L);
        receiver.setCurrency("USD");
        receiver.setBalance(new BigDecimal("100.00"));

        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setSenderAccountId(1L);
        request.setReceiverAccountId(2L);
        request.setAmount(new BigDecimal("10"));

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(receiver));
        when(config.getTransactionFeePercentage()).thenReturn(new BigDecimal("0.01"));

        transactionService.transfer(request);

        assertEquals(new BigDecimal("89.90"), sender.getBalance());
        assertEquals(new BigDecimal("110.00"), receiver.getBalance());
        verify(accountRepository, times(1)).save(sender);
        verify(accountRepository, times(1)).save(receiver);
        verify(auditService, times(1)).logTransaction(eq(1L), anyString(), contains("Transferred 10"));
    }

    @Test
    public void testTransferSuccessfulUSDtoAUD() {
        Account sender = new Account();
        sender.setId(1L);
        sender.setCurrency("USD");
        sender.setBalance(new BigDecimal("100.00"));

        Account receiver = new Account();
        receiver.setId(2L);
        receiver.setCurrency("AUD");
        receiver.setBalance(new BigDecimal("100.00"));

        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setSenderAccountId(1L);
        request.setReceiverAccountId(2L);
        request.setAmount(new BigDecimal("10"));

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(receiver));
        when(fxRateService.getRate("USD", "AUD")).thenReturn(new BigDecimal("2.0"));
        when(config.getTransactionFeePercentage()).thenReturn(new BigDecimal("0.01"));

        transactionService.transfer(request);

        assertEquals(new BigDecimal("89.90"), sender.getBalance());
        assertEquals(new BigDecimal("120.00"), receiver.getBalance());
        verify(accountRepository, times(1)).save(sender);
        verify(accountRepository, times(1)).save(receiver);
        verify(auditService, times(1)).logTransaction(eq(1L), anyString(), contains("Transferred 10"));
    }

    @Test
    public void testTransferNegativeAmountException() {
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setSenderAccountId(1L);
        request.setReceiverAccountId(2L);
        request.setAmount(new BigDecimal("-10"));

        assertThrows(TSBadRequestException.class, () -> transactionService.transfer(request));
    }

    @Test
    public void testTransferSenderNotFoundException() {
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setSenderAccountId(1L);
        request.setReceiverAccountId(2L);
        request.setAmount(new BigDecimal("10"));

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThrows(TSBadRequestException.class, () -> transactionService.transfer(request));
    }

    @Test
    public void testTransferReceiverNotFoundException() {
        Account sender = new Account();
        sender.setId(1L);
        sender.setCurrency("USD");
        sender.setBalance(new BigDecimal("100.00"));

        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setSenderAccountId(1L);
        request.setReceiverAccountId(2L);
        request.setAmount(new BigDecimal("10"));

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.empty());

        assertThrows(TSBadRequestException.class, () -> transactionService.transfer(request));
    }


    @Test
    public void testTransferInsufficientBalanceException() {
        Account sender = new Account();
        sender.setId(1L);
        sender.setCurrency("USD");
        sender.setBalance(new BigDecimal("5.00"));

        Account receiver = new Account();
        receiver.setId(2L);
        receiver.setCurrency("USD");
        receiver.setBalance(new BigDecimal("100.00"));

        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setSenderAccountId(1L);
        request.setReceiverAccountId(2L);
        request.setAmount(new BigDecimal("10"));

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(receiver));
        when(config.getTransactionFeePercentage()).thenReturn(new BigDecimal("0.01"));

        assertThrows(TSBadRequestException.class, () -> transactionService.transfer(request));
    }
}