package WalletManager.dtos.response;

import lombok.Data;

@Data
public class AccountResponseDTO {
    private Long id;
    private String name;
    private Double balance;
}
