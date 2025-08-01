package com.ingcase.digitalwallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingcase.digitalwallet.exception.GlobalExceptionHandler;
import com.ingcase.digitalwallet.exception.TransactionNotFoundException;
import com.ingcase.digitalwallet.model.dto.DepositRequest;
import com.ingcase.digitalwallet.model.dto.TransactionResponse;
import com.ingcase.digitalwallet.model.dto.TransactionUpdateRequest;
import com.ingcase.digitalwallet.model.dto.WithdrawRequest;
import com.ingcase.digitalwallet.model.enums.OppositePartyType;
import com.ingcase.digitalwallet.model.enums.Status;
import com.ingcase.digitalwallet.model.enums.Type;
import com.ingcase.digitalwallet.service.TransactionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerTest {

    private MockMvc mockMvc;

    private Validator validator;

    private ObjectMapper objectMapper;

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper();
    }

    @Test
    void givenValidDepositRequest_whenDeposit_thenReturnTransaction() throws Exception {

        DepositRequest depositRequest = new DepositRequest(BigDecimal.valueOf(2000),1L,1L, OppositePartyType.IBAN,"TR123123");

        TransactionResponse transactionResponse = TransactionResponse.builder()
                .amount(BigDecimal.valueOf(2000))
                .type(Type.DEPOSIT)
                .oppositePartyType(OppositePartyType.IBAN)
                .oppositeParty("TR123123")
                .status(Status.PENDING)
                .build();

        when(transactionService.deposit(any(DepositRequest.class))).thenReturn(transactionResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(2000)))
                .andExpect(jsonPath("$.type").value(Type.DEPOSIT.toString()))
                .andExpect(jsonPath("$.oppositePartyType").value(OppositePartyType.IBAN.toString()))
                .andExpect(jsonPath("$.oppositeParty").value("TR123123"))
                .andExpect(jsonPath("$.status").value(Status.PENDING.toString()));

        verify(transactionService, times(1)).deposit(any(DepositRequest.class));
    }
    @Test
    void givenInvalidDepositRequest_whenDeposit_thenBadRequest() throws Exception {

        DepositRequest depositRequest = new DepositRequest(BigDecimal.valueOf(-2000),1L,1L, OppositePartyType.IBAN,"TR123123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest())
                .andExpect( result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void givenFieldsAllInvalidDepositRequest_whenDeposit_thenViolationsShouldBeFound() {
        DepositRequest depositRequest = new DepositRequest(BigDecimal.valueOf(-2000),null,null, null,null);

        Set<ConstraintViolation<DepositRequest>> violations = validator.validate(depositRequest);

        assertThat(violations).hasSize(violations.size());
        violations.forEach(v -> {
            System.out.printf("Field: %-30s ➝ Message: %s%n", v.getPropertyPath(), v.getMessage());
        });
    }

    @Test
    void givenFieldsAllValidWalletCreateRequest_whenCreateWallet_thenNoViolations() {
        DepositRequest depositRequest = new DepositRequest(BigDecimal.valueOf(2000),1L,1L, OppositePartyType.IBAN,"TR123123");

        Set<ConstraintViolation<DepositRequest>> violations = validator.validate(depositRequest);

        assertThat(violations).isEmpty();
    }

    @Test
    void givenValidWithdrawRequest_whenWithdraw_thenReturnTransaction() throws Exception {

        WithdrawRequest withdrawRequest = new WithdrawRequest(BigDecimal.valueOf(2000),1L,1L, OppositePartyType.IBAN,"TR123123");

        TransactionResponse transactionResponse = TransactionResponse.builder()
                .amount(BigDecimal.valueOf(2000))
                .type(Type.WITHDRAW)
                .oppositePartyType(OppositePartyType.IBAN)
                .oppositeParty("TR123123")
                .status(Status.PENDING)
                .build();

        when(transactionService.withdraw(any(WithdrawRequest.class))).thenReturn(transactionResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(2000)))
                .andExpect(jsonPath("$.type").value(Type.WITHDRAW.toString()))
                .andExpect(jsonPath("$.oppositePartyType").value(OppositePartyType.IBAN.toString()))
                .andExpect(jsonPath("$.oppositeParty").value("TR123123"))
                .andExpect(jsonPath("$.status").value(Status.PENDING.toString()));

        verify(transactionService, times(1)).withdraw(any(WithdrawRequest.class));
    }
    @Test
    void givenInvalidWithdrawRequest_whenWithdraw_thenBadRequest() throws Exception {

        WithdrawRequest withdrawRequest = new WithdrawRequest(BigDecimal.valueOf(-2000),1L,1L, OppositePartyType.IBAN,"TR123123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest())
                .andExpect( result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void givenValidCustomerIdAndWalletId_whenGetWalletTransactions_thenListedTransactions() throws Exception {

        List<TransactionResponse> transactionResponseList = List.of(TransactionResponse.builder()
                .amount(BigDecimal.valueOf(2000))
                .type(Type.WITHDRAW)
                .oppositePartyType(OppositePartyType.IBAN)
                .oppositeParty("TR123123")
                .status(Status.PENDING)
                .build());

        when(transactionService.getWalletTransactions(any(Long.class), any(Long.class))).thenReturn(transactionResponseList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions/list")
                        .header("walletId", 1L)
                        .header("customerId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(transactionService, times(1)).getWalletTransactions(any(Long.class), any(Long.class));
    }

    @Test
    void givenValidTransactionIdAndRequest_whenUpdateTransaction_thenUpdatedTransaction() throws Exception {
        TransactionUpdateRequest transactionUpdateRequest = new TransactionUpdateRequest(Status.APPROVED);

        TransactionResponse transactionResponse = TransactionResponse.builder()
                .amount(BigDecimal.valueOf(2000))
                .type(Type.WITHDRAW)
                .oppositePartyType(OppositePartyType.IBAN)
                .oppositeParty("TR123123")
                .status(Status.APPROVED)
                .build();

        when(transactionService.updateTransaction(any(Long.class), any(TransactionUpdateRequest.class))).thenReturn(transactionResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/transactions/update/{transactionId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(2000)))
                .andExpect(jsonPath("$.type").value("WITHDRAW"))
                .andExpect(jsonPath("$.oppositePartyType").value(OppositePartyType.IBAN.toString()))
                .andExpect(jsonPath("$.oppositeParty").value("TR123123"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(transactionService, times(1)).updateTransaction(any(Long.class), any(TransactionUpdateRequest.class));
    }

    @Test
    void givenTransactionWithNonPendingStatus_whenUpdateTransaction_thenReturnsMessage() throws Exception {

        TransactionUpdateRequest transactionUpdateRequest = new TransactionUpdateRequest(Status.APPROVED);

        when(transactionService.updateTransaction(any(Long.class), any(TransactionUpdateRequest.class)))
                .thenThrow(new TransactionNotFoundException("Pending transaction not found"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/transactions/update/{transactionId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionUpdateRequest)))
                .andExpect(status().isNotFound())
                .andExpect( result -> assertTrue(result.getResolvedException() instanceof TransactionNotFoundException));

        verify(transactionService, times(1)).updateTransaction(any(Long.class), any(TransactionUpdateRequest.class));
    }
}
