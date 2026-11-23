package WalletManager.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AccountRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotNull(message = "El monto es obligatorio")
    @PositiveOrZero(message = "El monto debe ser cero o mayor")
    private Double balance;
}
