package com.ingcase.digitalwallet.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ingcase.digitalwallet.model.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    @JsonIgnore
    private Long id;
    private String walletName;
    private Currency currency;
    private Boolean activeForShopping;
    private Boolean activeForWithdraw;
    private BigDecimal balance;
    private BigDecimal usableBalance;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createDate;
}
