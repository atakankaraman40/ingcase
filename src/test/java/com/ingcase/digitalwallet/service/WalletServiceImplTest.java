package com.ingcase.digitalwallet.service;

import com.ingcase.digitalwallet.mapper.WalletMapper;
import com.ingcase.digitalwallet.model.dto.WalletCreateRequest;
import com.ingcase.digitalwallet.model.dto.WalletResponse;
import com.ingcase.digitalwallet.model.entity.Customer;
import com.ingcase.digitalwallet.model.entity.Wallet;
import com.ingcase.digitalwallet.model.enums.Currency;
import com.ingcase.digitalwallet.model.enums.Role;
import com.ingcase.digitalwallet.repository.WalletRepository;
import com.ingcase.digitalwallet.service.impl.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WalletServiceImplTest {

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private CustomerService customerService;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidWalletCreateRequest_whenCreateWallet_thenWalletIsCreated() {
        WalletCreateRequest walletCreateRequest = new WalletCreateRequest("Test Wallet", Currency.EUR, true, true, 1L);

        Customer customer = new Customer(1L,"Atakan","Karaman","TR123", Role.CUSTOMER, null);

        Wallet actualWallet = Wallet.builder()
                .walletName(walletCreateRequest.getWalletName())
                .currency(walletCreateRequest.getCurrency())
                .activeForShopping(true)
                .activeForWithdraw(true)
                .balance(BigDecimal.ZERO)
                .usableBalance(BigDecimal.ZERO)
                .build();

        WalletResponse expectedWallet = WalletResponse.builder()
                .walletName("Test Wallet")
                .currency(Currency.EUR)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .balance(BigDecimal.ZERO)
                .usableBalance(BigDecimal.ZERO)
                .build();

        when(customerService.findById(1L)).thenReturn(customer);
        when(walletRepository.save(any(Wallet.class))).thenReturn(actualWallet);
        when(walletMapper.toDto(actualWallet)).thenReturn(expectedWallet);

        WalletResponse result = walletService.createWallet(walletCreateRequest);

        assertThat(result).isEqualTo(expectedWallet);
        verify(customerService, times(1)).findById(1L);
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(walletMapper, times(1)).toDto(actualWallet);
    }

    @Test
    void givenValidCustomerId_whenGetCustomerWallets_thenReturnWalletList() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .walletName("Test Wallet")
                .currency(Currency.EUR)
                .balance(BigDecimal.ZERO)
                .usableBalance(BigDecimal.ZERO)
                .build();

        List<Wallet> walletList = List.of(wallet);

        WalletResponse walletResponse = WalletResponse.builder()
                .id(1L)
                .walletName("Test Wallet")
                .currency(Currency.EUR)
                .balance(BigDecimal.ZERO)
                .usableBalance(BigDecimal.ZERO)
                .build();

        when(walletRepository.findByCustomerId(1l)).thenReturn(walletList);
        when(walletMapper.toDto(wallet)).thenReturn(walletResponse);

        List<WalletResponse> result = walletService.getCustomerWallets(1l);

        assertThat(result).hasSize(1);
        assertThat(result).contains(walletResponse);
        verify(walletRepository, times(1)).findByCustomerId(1l);
        verify(walletMapper, times(1)).toDto(wallet);
    }

}
