package com.ingcase.digitalwallet.service;

import com.ingcase.digitalwallet.model.dto.WalletCreateRequest;
import com.ingcase.digitalwallet.model.dto.WalletResponse;
import com.ingcase.digitalwallet.model.entity.Wallet;

import java.util.List;

public interface WalletService {

    WalletResponse createWallet(WalletCreateRequest walletCreateRequest);

    List<WalletResponse> getCustomerWallets(Long customerId);

    void save(Wallet wallet);

    Wallet findByTransactionId(Long transactionId);

    Wallet findByCustomerIdAndId(Long customerId, Long id);
}
