package com.ingcase.digitalwallet.service;

import com.ingcase.digitalwallet.model.dto.DepositRequest;
import com.ingcase.digitalwallet.model.dto.TransactionResponse;
import com.ingcase.digitalwallet.model.dto.TransactionUpdateRequest;
import com.ingcase.digitalwallet.model.dto.WithdrawRequest;

import java.util.List;

public interface TransactionService {

    TransactionResponse withdraw(WithdrawRequest withdrawRequest);

    TransactionResponse deposit(DepositRequest DepositRequest);

    List<TransactionResponse> getWalletTransactions(Long walletId ,Long customerId);

    TransactionResponse updateTransaction(Long transactionId , TransactionUpdateRequest transactionUpdateRequest);
}
