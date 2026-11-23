package WalletManager.services;

import WalletManager.dtos.requests.AccountRequestDTO;
import WalletManager.models.Account;

import java.util.List;

public interface AccountService {

    List<Account> getAllAccounts();

    Account getAccountById(Long id);

    Account createAccount(AccountRequestDTO dto);

    Account updateAccount(Long id, AccountRequestDTO dto);

    void deleteAccount(Long id);
}