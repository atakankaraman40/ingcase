package com.ingcase.digitalwallet.model.dto;

import com.ingcase.digitalwallet.model.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class TransactionUpdateRequest {

    @NotNull(message = "{transactionUpdateDTO.status.not-null}")
    Status status;
}
