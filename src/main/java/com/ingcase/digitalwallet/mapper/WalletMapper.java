package com.ingcase.digitalwallet.mapper;

import com.ingcase.digitalwallet.model.dto.WalletResponse;
import com.ingcase.digitalwallet.model.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletResponse toDto(Wallet wallet);
}
