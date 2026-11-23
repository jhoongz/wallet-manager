package WalletManager.services.impl;

import WalletManager.dtos.requests.DashboardSummaryDTO;
import WalletManager.dtos.requests.TransactionRequestDTO;
import WalletManager.mappers.TransactionMapper;
import WalletManager.models.Account;
import WalletManager.models.Transaction;
import WalletManager.models.enums.TransactionType;
import WalletManager.repositories.AccountRepository;
import WalletManager.repositories.TransactionRepository;
import WalletManager.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionRequestDTO dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setAccount(account);

        if (dto.getType() == TransactionType.PAGO_GASTO || dto.getType() == TransactionType.TRANSFERENCIA_SALIENTE) {
            account.setBalance(account.getBalance() - dto.getAmount());
        } else {
            account.setBalance(account.getBalance() + dto.getAmount());
        }

        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }

    @Override
    public DashboardSummaryDTO getMonthlySummary(int month, int year) {
        List<Transaction> transactions = transactionRepository.findByMonthAndYear(month, year);

        double totalCobros = 0.0;
        double totalPagos = 0.0;
        double totalRetribuciones = 0.0;
        double transferenciasPositivas = 0.0;
        double transferenciasNegativas = 0.0;

        for (Transaction t : transactions) {
            switch (t.getType()) {
                case COBRO_ACTIVO -> totalCobros += t.getAmount();
                case PAGO_GASTO -> totalPagos += t.getAmount();
                case RETRIBUCION_PASIVA -> totalRetribuciones += t.getAmount();
                case TRANSFERENCIA_ENTRANTE -> transferenciasPositivas += t.getAmount();
                case TRANSFERENCIA_SALIENTE -> transferenciasNegativas += t.getAmount();
            }
        }

        double flujoNeto = (totalCobros + totalRetribuciones) - totalPagos;

        return DashboardSummaryDTO.builder()
                .totalCobros(totalCobros)
                .totalPagos(totalPagos)
                .totalRetribuciones(totalRetribuciones)
                .transferenciasPositivas(transferenciasPositivas)
                .transferenciasNegativas(transferenciasNegativas)
                .flujoNeto(flujoNeto)
                .build();
    }

    @Override
    @Transactional
    public void importFromCsv(MultipartFile file, Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + accountId));

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split("[,;]");

                if (data.length >= 4) {
                    try {
                        TransactionRequestDTO dto = new TransactionRequestDTO();
                        dto.setDate(LocalDate.parse(data[0].trim()));
                        dto.setDescription(data[1].trim());
                        dto.setAmount(Double.parseDouble(data[2].trim()));
                        dto.setType(TransactionType.valueOf(data[3].trim().toUpperCase()));

                        dto.setAccountId(accountId);

                        this.createTransaction(dto);
                    } catch (Exception e) {
                        System.err.println("❌ Error procesando la línea " + lineNumber + ": " + line);
                        System.err.println("Detalle del error: " + e.getMessage());
                        throw new RuntimeException("Error en la línea " + lineNumber + ": " + e.getMessage());
                    }
                } else {
                    System.out.println("⚠️ Línea " + lineNumber + " ignorada (tiene " + data.length + " columnas, se esperaban 4): " + line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Long id, TransactionRequestDTO dto) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        Account oldAccount = existingTransaction.getAccount();

        if (existingTransaction.getType() == TransactionType.PAGO_GASTO || existingTransaction.getType() == TransactionType.TRANSFERENCIA_SALIENTE) {
            oldAccount.setBalance(oldAccount.getBalance() + existingTransaction.getAmount()); // Era resta, ahora suma
        } else {
            oldAccount.setBalance(oldAccount.getBalance() - existingTransaction.getAmount()); // Era suma, ahora resta
        }
        accountRepository.save(oldAccount);

        Account newAccount = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Nueva cuenta no encontrada"));

        existingTransaction.setDescription(dto.getDescription());
        existingTransaction.setAmount(dto.getAmount());
        existingTransaction.setDate(dto.getDate());
        existingTransaction.setType(dto.getType());
        existingTransaction.setAccount(newAccount);

        if (dto.getType() == TransactionType.PAGO_GASTO || dto.getType() == TransactionType.TRANSFERENCIA_SALIENTE) {
            newAccount.setBalance(newAccount.getBalance() - dto.getAmount());
        } else {
            newAccount.setBalance(newAccount.getBalance() + dto.getAmount());
        }
        accountRepository.save(newAccount);

        return transactionRepository.save(existingTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        Account account = transaction.getAccount();

        if (transaction.getType() == TransactionType.PAGO_GASTO || transaction.getType() == TransactionType.TRANSFERENCIA_SALIENTE) {
            account.setBalance(account.getBalance() + transaction.getAmount());
        } else {
            account.setBalance(account.getBalance() - transaction.getAmount());
        }

        accountRepository.save(account);
        transactionRepository.delete(transaction);
    }
}
