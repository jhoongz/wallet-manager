package WalletManager.mappers;

import WalletManager.dtos.requests.TransactionRequestDTO;
import WalletManager.dtos.response.TransactionResponseDTO;
import WalletManager.models.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "id", ignore = true)
    Transaction toEntity(TransactionRequestDTO dto);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "account.name", target = "accountName")
    TransactionResponseDTO toResponse(Transaction transaction);
}
