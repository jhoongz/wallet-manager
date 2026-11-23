package WalletManager.services;

import WalletManager.dtos.requests.DashboardSummaryDTO;
import WalletManager.dtos.requests.TransactionRequestDTO;
import WalletManager.models.Transaction;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TransactionService {
    List<Transaction> getAllTransactions();

    Transaction createTransaction(TransactionRequestDTO dto);

    DashboardSummaryDTO getMonthlySummary(int month, int year);

    void importFromCsv(MultipartFile file, Long accountId);

    Transaction updateTransaction(Long id, TransactionRequestDTO dto);

    void deleteTransaction(Long id);
}