package com.ingcase.digitalwallet.service.impl;

import com.ingcase.digitalwallet.exception.InsufficientBalanceException;
import com.ingcase.digitalwallet.exception.PaymentNotAllowedException;
import com.ingcase.digitalwallet.exception.TransactionNotFoundException;
import com.ingcase.digitalwallet.exception.TransferNotAllowedException;
import com.ingcase.digitalwallet.mapper.TransactionMapper;
import com.ingcase.digitalwallet.model.dto.DepositRequest;
import com.ingcase.digitalwallet.model.dto.TransactionResponse;
import com.ingcase.digitalwallet.model.dto.TransactionUpdateRequest;
import com.ingcase.digitalwallet.model.dto.WithdrawRequest;
import com.ingcase.digitalwallet.model.entity.Transaction;
import com.ingcase.digitalwallet.model.entity.Wallet;
import com.ingcase.digitalwallet.model.enums.OppositePartyType;
import com.ingcase.digitalwallet.model.enums.Status;
import com.ingcase.digitalwallet.model.enums.Type;
import com.ingcase.digitalwallet.repository.TransactionRepository;
import com.ingcase.digitalwallet.service.TransactionService;
import com.ingcase.digitalwallet.service.WalletService;
import com.ingcase.digitalwallet.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse withdraw(WithdrawRequest withdrawRequest) {
        Wallet wallet = walletService.findByCustomerIdAndId(withdrawRequest.getCustomerId(), withdrawRequest.getWalletId());

        final BigDecimal requestedAmount = withdrawRequest.getAmount();
        validateWithdraw(withdrawRequest, wallet,requestedAmount);
        Status status = determineStatus(requestedAmount);
        updateWalletBalanceForWithdraw(wallet, status, requestedAmount);

        TransactionResponse transactionResponse = TransactionResponse.builder()
                .amount(requestedAmount)
                .type(Type.WITHDRAW)
                .oppositePartyType(withdrawRequest.getOppositePartyType())
                .oppositeParty(withdrawRequest.getOppositeParty())
                .status(status)
                .wallet(wallet)
                .build();

        transactionRepository.save(transactionMapper.toEntity(transactionResponse));

        return transactionResponse;
    }

    @Transactional
    public TransactionResponse deposit(DepositRequest depositRequest) {

        Wallet wallet = walletService.findByCustomerIdAndId(depositRequest.getCustomerId(), depositRequest.getWalletId());
        Status status = determineStatus(depositRequest.getAmount());

        updateWalletBalanceForDeposit(wallet, status, depositRequest.getAmount());

        TransactionResponse transactionResponse = TransactionResponse.builder()
                .amount(depositRequest.getAmount())
                .type(Type.DEPOSIT)
                .oppositePartyType(depositRequest.getOppositePartyType())
                .oppositeParty(depositRequest.getOppositeParty())
                .status(status)
                .wallet(wallet)
                .build();
        Transaction transaction = transactionMapper.toEntity(transactionResponse);

        transactionRepository.save(transaction);

        return transactionResponse;
    }

    public List<TransactionResponse> getWalletTransactions(Long walletId, Long customerId) {
        Wallet wallet = walletService.findByCustomerIdAndId(customerId, walletId);
        List<Transaction> transaction = transactionRepository.findAllByWalletId(wallet.getId());
        return transaction.stream().map(transactionMapper::toDto).toList();
    }

    @Transactional
    public TransactionResponse updateTransaction(Long transactionId, TransactionUpdateRequest transactionUpdateRequest) {
        validateStatus(transactionUpdateRequest.getStatus());

        Transaction transaction = transactionRepository.findByIdAndStatusPending(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Pending transaction not found"));

        Wallet wallet = walletService.findByTransactionId(transactionId);
        updateWalletBalanceForApproval(wallet, transactionUpdateRequest.getStatus(), transaction);

        transaction.setStatus(transactionUpdateRequest.getStatus());
        transactionRepository.updateStatusById(transactionId, transactionUpdateRequest.getStatus());

        return transactionMapper.toDto(transaction);
    }

    private void updateWalletBalanceForApproval(Wallet wallet, Status status, Transaction transaction) {
        if(status == Status.APPROVED) {
            if(transaction.getType().equals(Type.WITHDRAW)) {
                wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            } else {
                wallet.setUsableBalance(wallet.getUsableBalance().add(transaction.getAmount()));
            }
        } else if(status == Status.DENIED) {
            if(transaction.getType().equals(Type.WITHDRAW)) {
                wallet.setUsableBalance(wallet.getUsableBalance().add(transaction.getAmount()));
            } else {
                wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            }
        }
        walletService.save(wallet);
    }

    private void updateWalletBalanceForDeposit(Wallet wallet, Status status, BigDecimal amount) {
        if(status == Status.APPROVED) {
            wallet.setBalance(wallet.getBalance().add(amount));
            wallet.setUsableBalance(wallet.getUsableBalance().add(amount));
        } else {
            wallet.setBalance(wallet.getBalance().add(amount));
        }
        walletService.save(wallet);
    }

    private void updateWalletBalanceForWithdraw(Wallet wallet, Status status, BigDecimal amount) {
        if(status == Status.APPROVED) {
            wallet.setBalance(wallet.getBalance().subtract(amount));
            wallet.setUsableBalance(wallet.getUsableBalance().subtract(amount));
        } else {
            wallet.setUsableBalance(wallet.getUsableBalance().subtract(amount));
        }
        walletService.save(wallet);
    }

    private void validateWithdraw(WithdrawRequest withdrawRequest, Wallet wallet, BigDecimal requestedAmount) {
        boolean isPayment = OppositePartyType.PAYMENT.equals(withdrawRequest.getOppositePartyType());
        if(isPayment && !wallet.getActiveForShopping()) {
            throw new PaymentNotAllowedException("Payment is not allowed for this wallet");
        }

        boolean isTransfer = OppositePartyType.IBAN.equals(withdrawRequest.getOppositePartyType());
        if(isTransfer && !wallet.getActiveForWithdraw()) {
            throw new TransferNotAllowedException("Transfer is not allowed for this wallet");
        }

        if(wallet.getUsableBalance().compareTo(requestedAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to complete the withdraw");
        }
    }

    private Status determineStatus(BigDecimal amount) {
        return amount.compareTo(AppConstants.THRESHOLD_AMOUNT) > 0 ? Status.PENDING : Status.APPROVED;
    }

    private void validateStatus(Status status) {
        if (status.equals(Status.PENDING)) {
            throw new IllegalArgumentException("Transaction status cannot be set to PENDING");
        }
    }
}
