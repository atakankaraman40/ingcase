package com.ingcase.digitalwallet.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ingcase.digitalwallet.model.entity.Wallet;
import com.ingcase.digitalwallet.model.enums.OppositePartyType;
import com.ingcase.digitalwallet.model.enums.Status;
import com.ingcase.digitalwallet.model.enums.Type;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionResponse {

    private BigDecimal amount;
    private Type type;
    private OppositePartyType oppositePartyType;
    private String oppositeParty;
    private Status status;

    @JsonIgnore
    private Wallet wallet;
}
