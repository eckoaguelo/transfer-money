package com.transaction.controller;

import com.transaction.request.TransferMoneyRequest;
import com.transaction.service.interfaces.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("/api/transfer")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<String> transfer(@RequestBody TransferMoneyRequest request){
        log.info("START: transfer: {}", request.toString());

        transactionService.transfer(request);

        log.info("END: transfer");
        return ResponseEntity.ok("Transfer successful");
    }
}