package com.martins.martinsbank.service;

import com.martins.martinsbank.dto.TransactionDto;
import com.martins.martinsbank.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);

}
