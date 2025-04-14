package Z_CodePartageEntreApps.Windows.B.Windows.Options.Ui

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import Z_CodePartageEntreApps.Windows.B.Windows.Options.A_OptionsControlsButtons_Main
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun AffichePersonsPV() {
    // Create mock data for preview
    val mockUiState = VendeursUiState(
        vendeurs = listOf(
            _1_5_Vendeur(vid = 1, nom = "Vendeur 1"),
            _1_5_Vendeur(vid = 2, nom = "Vendeur 2")
        ),
        periodes = listOf(
            _1_4_PeriodeVent(vid = 1, heurDebutInString = "10:00"),
            _1_4_PeriodeVent(vid = 2, heurDebutInString = "14:00")
        ),
        activeVendeurId = 1,
        activePeriodeId = 1
    )

    Column {
        VendeursContent(
            uiState = mockUiState,
            onVendeurSelected = {},
            onPeriodeSelected = {}
        )
    }
    A_OptionsControlsButtons_Main()
}

@Preview
@Composable
private fun AffichePersonsPV() {
    // Create mock data for preview
    val mockUiState = VendeursUiState(
        vendeurs = listOf(
            _1_5_Vendeur(vid = 1, nom = "Vendeur 1"),
            _1_5_Vendeur(vid = 2, nom = "Vendeur 2")
        ),
        periodes = listOf(
            _1_4_PeriodeVent(vid = 1, heurDebutInString = "10:00"),
            _1_4_PeriodeVent(vid = 2, heurDebutInString = "14:00")
        ),
        activeVendeurId = 1,
        activePeriodeId = 1
    )

    Column {
        VendeursContent(
            uiState = mockUiState,
            onVendeurSelected = {},
            onPeriodeSelected = {}
        )
    }
    A_OptionsControlsButtons_Main()
}
