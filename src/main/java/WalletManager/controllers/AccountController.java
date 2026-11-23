package WalletManager.controllers;

import WalletManager.dtos.requests.AccountRequestDTO;
import WalletManager.dtos.response.AccountResponseDTO;
import WalletManager.mappers.AccountMapper;
import WalletManager.models.Account;
import WalletManager.repositories.AccountRepository;
import WalletManager.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountMapper accountMapper;

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        List<AccountResponseDTO> accounts = accountService.getAllAccounts().stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return new ResponseEntity<>(accountMapper.toResponse(account), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO dto) {
        Account savedAccount = accountService.createAccount(dto);
        return new ResponseEntity<>(accountMapper.toResponse(savedAccount), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestDTO dto) {
        Account updatedAccount = accountService.updateAccount(id, dto);
        return new ResponseEntity<>(accountMapper.toResponse(updatedAccount), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return new ResponseEntity<>("Cuenta eliminada correctamente", HttpStatus.OK);
    }
}