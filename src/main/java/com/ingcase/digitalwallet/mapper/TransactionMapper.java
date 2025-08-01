package com.ingcase.digitalwallet.mapper;

import com.ingcase.digitalwallet.model.dto.TransactionResponse;
import com.ingcase.digitalwallet.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionResponse toDto(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    Transaction toEntity(TransactionResponse transactionResponse);


}
