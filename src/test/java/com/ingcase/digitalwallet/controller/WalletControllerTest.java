package com.ingcase.digitalwallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingcase.digitalwallet.exception.GlobalExceptionHandler;
import com.ingcase.digitalwallet.model.dto.WalletCreateRequest;
import com.ingcase.digitalwallet.model.dto.WalletResponse;
import com.ingcase.digitalwallet.model.enums.Currency;
import com.ingcase.digitalwallet.service.WalletService;
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

public class WalletControllerTest {

    private MockMvc mockMvc;

    private Validator validator;

    private ObjectMapper objectMapper;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper();
    }

    @Test
    void givenValidWalletCreateRequest_whenCreateWallet_thenCreatedWallet() throws Exception {

        WalletCreateRequest walletCreateRequest = new WalletCreateRequest("Test Wallet", Currency.EUR, true, true, 1L);

        WalletResponse walletResponse = WalletResponse.builder()
                .walletName("Test Wallet")
                .currency(Currency.EUR)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .balance(BigDecimal.ZERO)
                .usableBalance(BigDecimal.ZERO)
                .build();

        when(walletService.createWallet(any(WalletCreateRequest.class))).thenReturn(walletResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.walletName").value("Test Wallet"))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.activeForShopping").value(true))
                .andExpect(jsonPath("$.activeForWithdraw").value(true))
                .andExpect(jsonPath("$.balance").value(BigDecimal.ZERO))
                .andExpect(jsonPath("$.usableBalance").value(BigDecimal.ZERO));

        verify(walletService, times(1)).createWallet(any(WalletCreateRequest.class));
    }

    @Test
    void givenInvalidWalletCreateRequest_whenCreateWallet_thenBadRequest() throws Exception {

        WalletCreateRequest walletCreateRequest = new WalletCreateRequest(null, Currency.EUR, true, true, 1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect( result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void givenFieldsAllInvalidWalletCreateRequest_whenCreateWallet_thenViolationsShouldBeFound() {
        WalletCreateRequest walletCreateRequest = new WalletCreateRequest("", null, null, null, -1L);

        Set<ConstraintViolation<WalletCreateRequest>> violations = validator.validate(walletCreateRequest);

        assertThat(violations).hasSize(violations.size());
        violations.forEach(v -> {
            System.out.printf("Field: %-30s ➝ Message: %s%n", v.getPropertyPath(), v.getMessage());
        });
    }

    @Test
    void givenFieldsAllValidWalletCreateRequest_whenCreateWallet_thenNoViolations() {
        WalletCreateRequest walletCreateRequest = new WalletCreateRequest("Test Wallet", Currency.EUR, true, true, 1L);

        Set<ConstraintViolation<WalletCreateRequest>> violations = validator.validate(walletCreateRequest);

        assertThat(violations).isEmpty();
    }

    @Test
    void givenValidCustomerId_whenGetCustomerWallets_thenListedWallet() throws Exception {

        List<WalletResponse> walletResponseList = List.of(WalletResponse.builder()
                .id(1L)
                .walletName("Test Wallet")
                .currency(Currency.EUR)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .balance(BigDecimal.ZERO)
                .usableBalance(BigDecimal.ZERO)
                .build());

        when(walletService.getCustomerWallets(any(Long.class))).thenReturn(walletResponseList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallets/{customerId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(walletService, times(1)).getCustomerWallets(any(Long.class));
    }
}

