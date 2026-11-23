package WalletManager.services.impl;

import WalletManager.dtos.requests.AccountRequestDTO;
import WalletManager.mappers.AccountMapper;
import WalletManager.models.Account;
import WalletManager.repositories.AccountRepository;
import WalletManager.repositories.TransactionRepository;
import WalletManager.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    @Transactional
    public Account createAccount(AccountRequestDTO dto) {
        Account newAccount = accountMapper.toEntity(dto);
        return accountRepository.save(newAccount);
    }

    @Override
    @Transactional
    public Account updateAccount(Long id, AccountRequestDTO dto) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        existingAccount.setName(dto.getName());
        existingAccount.setBalance(dto.getBalance());

        return accountRepository.save(existingAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        boolean hasTransactions = transactionRepository.existsByAccountId(id);
        if (hasTransactions) {
            throw new RuntimeException("No se puede eliminar la cuenta porque tiene transacciones registradas. Elimina las transacciones primero.");
        }

        accountRepository.delete(account);
    }
}
