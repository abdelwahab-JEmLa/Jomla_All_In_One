package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.SecID5FragID2UiState
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.text.SimpleDateFormat
import java.util.Locale

// 🔧 CORRECTION 2: Dans MainFilter.kt - S'assurer du tri
@Composable
fun MainFilter(
    uiState: SecID5FragID2UiState,
    dateStringName: DatesHandler,
    idClient: Long,
    viewModel: ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
) {
    Log.d("TransactionFilter", "=== DÉBUT DU FILTRE ===")
    Log.d("TransactionFilter", "ID Client recherché: $idClient")

    val filteredGroupedTransactions = remember(uiState.transactionsDateToList_C_3_BonAchate, idClient) {
        Log.d("TransactionFilter", "=== RECALCUL DU FILTRE ===")

        val result = uiState.transactionsDateToList_C_3_BonAchate
            .map { (period, transactions) ->
                val filteredTransactions = transactions
                    .filter { transaction ->
                        transaction.clientAcheteurID == idClient
                    }
                    // ✅ DOUBLE VÉRIFICATION: Re-tri par timestamp décroissant
                    .sortedByDescending { transaction ->
                        transaction.timestamps
                    }

                Log.d("TransactionFilter", "Période ${period.startDateInString}: ${filteredTransactions.size} transactions filtrées")
                filteredTransactions.forEachIndexed { index, transaction ->
                    Log.d("TransactionFilter", "  Position $index: VID=${transaction.vid}, Timestamp=${transaction.timestamps}")
                }

                Pair(period, filteredTransactions)
            }
            .filter { (_, transactions) -> transactions.isNotEmpty() }
            // ✅ CORRECTION: Tri des périodes par date décroissante
            .sortedByDescending { (period, _) ->
                try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(period.startDateInString)?.time ?: 0L
                } catch (e: Exception) {
                    Log.e("TransactionFilter", "Erreur parsing date: ${e.message}")
                    0L
                }
            }

        Log.d("TransactionFilter", "=== RÉSULTAT FINAL ===")
        Log.d("TransactionFilter", "Nombre de périodes: ${result.size}")

        result
    }

    MainList(filteredGroupedTransactions, dateStringName, viewModel, uiState, idClient)
}
