package com.ingcase.digitalwallet.model.dto;

import com.ingcase.digitalwallet.model.enums.OppositePartyType;
import jakarta.validation.constraints.*;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class WithdrawRequest {

    @NotNull(message = "{withdraw.amount.not-null}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{withdraw.amount.positive}")
    @Digits(integer = 15, fraction = 2, message = "{withdraw.amount.digits}")
    BigDecimal amount;

    @NotNull(message = "{withdraw.walletId.not-null}")
    @Positive(message = "{withdraw.walletId.positive}")
    Long walletId;

    @NotNull(message = "{withdraw.customerId.not-null}")
    @Positive(message = "{withdraw.customerId.positive}")
    Long customerId;
    OppositePartyType oppositePartyType;

    @NotBlank(message = "{withdraw.oppositeparty.not-blank}")
    @Size(max = 32, message = "{withdraw.oppositeparty.size}")
    String oppositeParty;
}
