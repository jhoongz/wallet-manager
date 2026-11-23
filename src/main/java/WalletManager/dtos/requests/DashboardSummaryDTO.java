package WalletManager.dtos.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryDTO {
    private Double totalCobros;
    private Double totalPagos;
    private Double totalRetribuciones;
    private Double transferenciasPositivas;
    private Double transferenciasNegativas;
    private Double flujoNeto;
}