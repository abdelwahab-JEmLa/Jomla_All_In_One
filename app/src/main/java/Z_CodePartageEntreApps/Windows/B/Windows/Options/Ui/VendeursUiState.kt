package Z_CodePartageEntreApps.Windows.B.Windows.Options.Ui

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur

// Data class to represent UI state
data class VendeursUiState(
    val vendeurs: List<_1_5_Vendeur> = emptyList(),
    val periodes: List<_1_4_PeriodeVent> = emptyList(),
    val activeVendeurId: Long = 0L,
    val activePeriodeId: Long = 0L,
)
