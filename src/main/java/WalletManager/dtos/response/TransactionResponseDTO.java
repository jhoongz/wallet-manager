package WalletManager.dtos.response;

import WalletManager.models.enums.TransactionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionResponseDTO {
    private Long id;
    private String description;
    private Double amount;
    private LocalDate date;
    private TransactionType type;
    private Long accountId;
    private String accountName;
}
