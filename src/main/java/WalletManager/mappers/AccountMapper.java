package WalletManager.mappers;

import WalletManager.dtos.requests.AccountRequestDTO;
import WalletManager.dtos.response.AccountResponseDTO;
import WalletManager.models.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    Account toEntity(AccountRequestDTO dto);

    AccountResponseDTO toResponse(Account account);
}