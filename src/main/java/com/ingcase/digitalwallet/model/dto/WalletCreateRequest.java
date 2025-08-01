package com.ingcase.digitalwallet.model.dto;

import com.ingcase.digitalwallet.model.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class WalletCreateRequest {

    @NotBlank(message = "{wallet.walletname.not-blank}")
    @Size(min = 3, max = 32, message = "{wallet.walletname.size}")
    String walletName;

    @NotNull(message = "{wallet.currency.not-null}")
    Currency currency;

    @NotNull(message = "{wallet.activeforshopping.not-null}")
    Boolean activeForShopping;

    @NotNull(message = "{wallet.activeforwithdraw.not-null}")
    Boolean activeForWithdraw;

    @NotNull(message = "{wallet.customerId.not-null}")
    Long customerId;
}
