package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.ViewModel

import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_BonAchate

data class UiState(
    val c3_BonAchate: List<C3_BonAchate> = emptyList(),

    val mainLoadingProgressPJuin3: Float = 0f,
)
