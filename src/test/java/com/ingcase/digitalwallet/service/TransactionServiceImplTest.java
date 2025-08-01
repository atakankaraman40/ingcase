package com.ingcase.digitalwallet.service;

import com.ingcase.digitalwallet.exception.InsufficientBalanceException;
import com.ingcase.digitalwallet.exception.PaymentNotAllowedException;
import com.ingcase.digitalwallet.exception.TransferNotAllowedException;
import com.ingcase.digitalwallet.mapper.TransactionMapper;
import com.ingcase.digitalwallet.model.dto.TransactionResponse;
import com.ingcase.digitalwallet.model.dto.TransactionUpdateRequest;
import com.ingcase.digitalwallet.model.dto.WithdrawRequest;
import com.ingcase.digitalwallet.model.entity.Customer;
import com.ingcase.digitalwallet.model.entity.Transaction;
import com.ingcase.digitalwallet.model.entity.Wallet;
import com.ingcase.digitalwallet.model.enums.*;
import com.ingcase.digitalwallet.repository.TransactionRepository;
import com.ingcase.digitalwallet.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Customer expectedCustomer;
    private Wallet expectedWallet;
    private Transaction expectedTransaction;

    @BeforeEach
    void setUp() {
        expectedCustomer = new Customer(1L,"Atakan","Karaman","TR123", Role.CUSTOMER, null);

        expectedWallet = Wallet.builder()
                .id(1L)
                .walletName("Test Wallet")
                .currency(Currency.TRY)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .balance(BigDecimal.valueOf(5000))
                .usableBalance(BigDecimal.valueOf(5000))
                .customer(expectedCustomer)
                .build();

        expectedTransaction = Transaction.builder()
                .id(1L)
                .wallet(expectedWallet)
                .amount(BigDecimal.valueOf(100))
                .type(Type.WITHDRAW)
                .oppositePartyType("IBAN")
                .oppositeParty("TR123123")
                .status(Status.PENDING)
                .build();
    }

    @Test
    void givenValidWithdrawRequest_whenWithdraw_thenReturnTransaction() {
        WithdrawRequest withdrawRequest = new WithdrawRequest(
                BigDecimal.valueOf(100),
                1L,
                1L,
                OppositePartyType.IBAN,
                "TR123123"
        );

        when(walletService.findByCustomerIdAndId(anyLong(), anyLong())).thenReturn(expectedWallet);
        when(transactionMapper.toEntity(any(TransactionResponse.class))).thenReturn(expectedTransaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        TransactionResponse result = transactionService.withdraw(withdrawRequest);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(result.getType()).isEqualTo(Type.WITHDRAW);
        assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(result.getOppositePartyType()).isEqualTo(OppositePartyType.IBAN);
        assertThat(result.getOppositeParty()).isEqualTo("TR123123");

        verify(walletService).findByCustomerIdAndId(1L, 1L);
        verify(walletService).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
        verify(walletService).save(argThat(wallet ->
                wallet.getBalance().equals(BigDecimal.valueOf(4900)) &&
                        wallet.getUsableBalance().equals(BigDecimal.valueOf(4900))
        ));
    }

    @Test
    void givenValidWithdrawRequest_whenWithdraw_thenPendingTransaction() {
        WithdrawRequest withdrawRequest = new WithdrawRequest(
                BigDecimal.valueOf(2000),
                1L,
                1L,
                OppositePartyType.IBAN,
                "TR123123"
        );

        when(walletService.findByCustomerIdAndId(1L, 1L)).thenReturn(expectedWallet);
        when(transactionMapper.toEntity(any(TransactionResponse.class))).thenReturn(expectedTransaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        TransactionResponse result = transactionService.withdraw(withdrawRequest);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.PENDING);

        verify(walletService).save(argThat(wallet ->
                wallet.getBalance().equals(BigDecimal.valueOf(5000)) &&
                        wallet.getUsableBalance().equals(BigDecimal.valueOf(3000))
        ));
    }

    @Test
    void givenInsufficientBalance_whenWithdraw_thenThrowInsufficientBalanceException() {
        expectedWallet.setUsableBalance(BigDecimal.valueOf(50));
        WithdrawRequest withdrawRequest = new WithdrawRequest(
                BigDecimal.valueOf(100),
                1L,
                1L,
                OppositePartyType.IBAN,
                "TR123123"
        );

        when(walletService.findByCustomerIdAndId(1L, 1L)).thenReturn(expectedWallet);

        assertThatThrownBy(() -> transactionService.withdraw(withdrawRequest))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(walletService).findByCustomerIdAndId(1L, 1L);
        verify(walletService, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void givenShoppingFlagFalse_whenWithdraw_thenThrowPaymentNotAllowedException() {
        expectedWallet.setActiveForShopping(false);
        WithdrawRequest withdrawRequest = new WithdrawRequest(
                BigDecimal.valueOf(100),
                1L,
                1L,
                OppositePartyType.PAYMENT,
                "TR123123"
        );

        when(walletService.findByCustomerIdAndId(1L, 1L)).thenReturn(expectedWallet);

        assertThatThrownBy(() -> transactionService.withdraw(withdrawRequest))
                .isInstanceOf(PaymentNotAllowedException.class);

        verify(walletService).findByCustomerIdAndId(1L, 1L);
        verify(walletService, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void givenWithdrawFlagFalse_whenWithdraw_thenThrowTransferNotAllowedException() {
        expectedWallet.setActiveForWithdraw(false);
        WithdrawRequest withdrawRequest = new WithdrawRequest(
                BigDecimal.valueOf(100),
                1L,
                1L,
                OppositePartyType.IBAN,
                "TR123123"
        );

        when(walletService.findByCustomerIdAndId(1L, 1L)).thenReturn(expectedWallet);

        assertThatThrownBy(() -> transactionService.withdraw(withdrawRequest))
                .isInstanceOf(TransferNotAllowedException.class);

        verify(walletService).findByCustomerIdAndId(1L, 1L);
        verify(walletService, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void givenPendingStatus_whenUpdateTransactionApproved_thenUpdateTransactionAndWallet() {
        TransactionUpdateRequest transactionUpdateRequest = new TransactionUpdateRequest(Status.APPROVED);
        Wallet walletWithPendingTransaction = Wallet.builder()
                .walletName("Test Wallet")
                .currency(Currency.TRY)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .balance(BigDecimal.valueOf(5000))
                .usableBalance(BigDecimal.valueOf(3000))
                .customer(expectedCustomer)
                .build();
        Transaction pendingTransaction = Transaction.builder()
                .wallet(walletWithPendingTransaction)
                .amount(BigDecimal.valueOf(2000))
                .type(Type.WITHDRAW)
                .oppositePartyType(OppositePartyType.IBAN.toString())
                .oppositeParty("TR123123")
                .status(Status.PENDING)
                .build();
        TransactionResponse expectedTransaction = TransactionResponse.builder()
                .amount(BigDecimal.valueOf(2000))
                .type(Type.WITHDRAW)
                .oppositePartyType(OppositePartyType.IBAN)
                .oppositeParty("TR123123")
                .status(Status.APPROVED)
                .wallet(walletWithPendingTransaction)
                .build();

        when(transactionRepository.findByIdAndStatusPending(1L)).thenReturn(Optional.of(pendingTransaction));
        when(walletService.findByTransactionId(1L)).thenReturn(walletWithPendingTransaction);
        when(transactionMapper.toDto(pendingTransaction)).thenReturn(expectedTransaction);

        TransactionResponse result = transactionService.updateTransaction(1L, transactionUpdateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
        verify(transactionRepository).findByIdAndStatusPending(1L);
        verify(walletService).findByTransactionId(1L);
        verify(transactionRepository).updateStatusById(1L, Status.APPROVED);
        verify(walletService).save(any(Wallet.class));
        verify(walletService).save(argThat(wallet ->
                    wallet.getBalance().equals(BigDecimal.valueOf(3000)) &&
                    wallet.getUsableBalance().equals(BigDecimal.valueOf(3000))
            ));
    }
    @Test
    void givenPendingStatus_whenUpdateTransactionDenied_thenUpdateTransactionAndWallet() {
        TransactionUpdateRequest transactionUpdateRequest = new TransactionUpdateRequest(Status.DENIED);
        Wallet walletWithPendingTransaction = Wallet.builder()
                .walletName("Test Wallet")
                .currency(Currency.TRY)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .balance(BigDecimal.valueOf(5000))
                .usableBalance(BigDecimal.valueOf(3000))
                .customer(expectedCustomer)
                .build();
        Transaction pendingTransaction = Transaction.builder()
                .wallet(walletWithPendingTransaction)
                .amount(BigDecimal.valueOf(2000))
                .type(Type.WITHDRAW)
                .oppositePartyType(OppositePartyType.IBAN.toString())
                .oppositeParty("TR123123")
                .status(Status.PENDING)
                .build();
        TransactionResponse expectedTransaction = TransactionResponse.builder()
                .amount(BigDecimal.valueOf(2000))
                .type(Type.WITHDRAW)
                .oppositePartyType(OppositePartyType.IBAN)
                .oppositeParty("TR123123")
                .status(Status.DENIED)
                .wallet(walletWithPendingTransaction)
                .build();

        when(transactionRepository.findByIdAndStatusPending(1L)).thenReturn(Optional.of(pendingTransaction));
        when(walletService.findByTransactionId(1L)).thenReturn(walletWithPendingTransaction);
        when(transactionMapper.toDto(pendingTransaction)).thenReturn(expectedTransaction);

        TransactionResponse result = transactionService.updateTransaction(1L, transactionUpdateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.DENIED);
        verify(transactionRepository).findByIdAndStatusPending(1L);
        verify(walletService).findByTransactionId(1L);
        verify(transactionRepository).updateStatusById(1L, Status.DENIED);
        verify(walletService).save(any(Wallet.class));
        verify(walletService).save(argThat(wallet ->
                wallet.getBalance().equals(BigDecimal.valueOf(5000)) &&
                        wallet.getUsableBalance().equals(BigDecimal.valueOf(5000))
        ));
    }
}
