package com.ingcase.digitalwallet.service.impl;

import com.ingcase.digitalwallet.exception.WalletNotFoundException;
import com.ingcase.digitalwallet.mapper.WalletMapper;
import com.ingcase.digitalwallet.model.dto.WalletCreateRequest;
import com.ingcase.digitalwallet.model.dto.WalletResponse;
import com.ingcase.digitalwallet.model.entity.Customer;
import com.ingcase.digitalwallet.model.entity.Wallet;
import com.ingcase.digitalwallet.repository.WalletRepository;
import com.ingcase.digitalwallet.service.CustomerService;
import com.ingcase.digitalwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final CustomerService customerService;
    private final WalletMapper walletMapper;

    @Transactional
    public WalletResponse createWallet(WalletCreateRequest walletCreateRequest) {

        Customer customer = customerService.findById(walletCreateRequest.getCustomerId());

        Wallet wallet = Wallet.builder()
                .walletName(walletCreateRequest.getWalletName())
                .currency(walletCreateRequest.getCurrency())
                .activeForShopping(walletCreateRequest.getActiveForShopping())
                .activeForWithdraw(walletCreateRequest.getActiveForWithdraw())
                .balance(BigDecimal.ZERO)
                .usableBalance(BigDecimal.ZERO)
                .customer(customer)
                .build();

        walletRepository.save(wallet);

        return walletMapper.toDto(wallet);
    }

    public List<WalletResponse> getCustomerWallets(Long customerId) {
        List<Wallet> wallets = walletRepository.findByCustomerId(customerId);
        return wallets.stream().map(walletMapper::toDto).toList();
    }

    @Transactional
    public void save(Wallet wallet) {
        walletRepository.save(wallet);
    }

    public Wallet findByTransactionId(Long transactionId) {
        return walletRepository.findByTransactionId(transactionId);
    }

    public Wallet findByCustomerIdAndId(Long customerId, Long id) {
        return walletRepository.findByCustomerIdAndId(customerId, id)
               .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
    }

}
