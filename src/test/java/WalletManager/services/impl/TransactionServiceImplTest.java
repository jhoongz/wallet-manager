package WalletManager.services.impl;

import WalletManager.dtos.requests.TransactionRequestDTO;
import WalletManager.mappers.TransactionMapper;
import WalletManager.models.Account;
import WalletManager.models.Transaction;
import WalletManager.models.enums.TransactionType;
import WalletManager.repositories.AccountRepository;
import WalletManager.repositories.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void createTransaction_Gasto_DebeRestarSaldoCorrectamente() {
        Account cuentaFalsa = new Account();
        cuentaFalsa.setId(1L);
        cuentaFalsa.setName("Mercado Pago");
        cuentaFalsa.setBalance(10000.0);

        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAccountId(1L);
        dto.setAmount(3000.0);
        dto.setType(TransactionType.PAGO_GASTO);
        dto.setDescription("Supermercado");
        dto.setDate(LocalDate.now());

        Transaction transaccionFalsa = new Transaction();
        transaccionFalsa.setAmount(3000.0);
        transaccionFalsa.setType(TransactionType.PAGO_GASTO);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(cuentaFalsa));
        when(transactionMapper.toEntity(dto)).thenReturn(transaccionFalsa);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        transactionService.createTransaction(dto);

        assertEquals(7000.0, cuentaFalsa.getBalance());
    }
}
