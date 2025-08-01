package com.ingcase.digitalwallet.model.dto;

import com.ingcase.digitalwallet.model.enums.OppositePartyType;
import jakarta.validation.constraints.*;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class DepositRequest {

    @NotNull(message = "{deposit.amount.not-null}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{deposit.amount.positive}")
    @Digits(integer = 15, fraction = 2, message = "{deposit.amount.digits}")
    BigDecimal amount;

    @NotNull(message = "{deposit.walletId.not-null}")
    @Positive(message = "{deposit.walletId.positive}")
    Long walletId;

    @NotNull(message = "{deposit.customerId.not-null}")
    @Positive(message = "{deposit.customerId.positive}")
    Long customerId;
    OppositePartyType oppositePartyType;

    @NotBlank(message = "{deposit.oppositeparty.not-blank}")
    @Size(max = 32, message = "{deposit.oppositeparty.size}")
    String oppositeParty;
}
