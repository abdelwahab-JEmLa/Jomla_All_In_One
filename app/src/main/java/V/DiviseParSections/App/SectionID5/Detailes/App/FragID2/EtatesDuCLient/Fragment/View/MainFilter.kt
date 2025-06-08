package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.SecID5FragID2UiState
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.text.SimpleDateFormat
import java.util.Locale

// 🔧 CORRECTION 1: Dans MainFilter - Améliorer le tri des transactions
@Composable
fun MainFilter(
    uiState: SecID5FragID2UiState,
    dateStringName: DatesHandler,
    idClient: Long,
    viewModel: ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
) {
    Log.d("TransactionFilter", "=== DÉBUT DU FILTRE ===")
    Log.d("TransactionFilter", "ID Client recherché: $idClient")
    Log.d("TransactionFilter", "Nombre total de périodes: ${uiState.transactionsDateToList_C_3_BonAchate.size}")

    val filteredGroupedTransactions = remember(uiState.transactionsDateToList_C_3_BonAchate, idClient) {
        Log.d("TransactionFilter", "=== DÉBUT DU FILTRAGE ===")

        val result = uiState.transactionsDateToList_C_3_BonAchate
            .map { (period, transactions) ->
                val filteredTransactions = transactions
                    .filter { transaction ->
                        val matches = transaction.clientAcheteurID == idClient
                        Log.d("TransactionFilter", "Transaction ${transaction.vid}: ClientID=${transaction.clientAcheteurID}, Recherché=$idClient, Match=$matches")
                        matches
                    }
                    // ✅ CORRECTION PRINCIPALE: Tri décroissant par timestamp
                    .sortedByDescending { transaction ->
                        Log.d("TransactionFilter", "Transaction ${transaction.vid} timestamp: ${transaction.timestamps}")
                        transaction.timestamps
                    }

                Log.d("TransactionFilter", "Période ${period.startDateInString}: ${filteredTransactions.size} transactions après filtrage et tri")

                // Log des transactions triées
                filteredTransactions.forEachIndexed { index, transaction ->
                    Log.d("TransactionFilter", "  Position $index: VID=${transaction.vid}, Timestamp=${transaction.timestamps}")
                }

                Pair(period, filteredTransactions)
            }
            .filter { (period, transactions) ->
                val hasTransactions = transactions.isNotEmpty()
                Log.d("TransactionFilter", "Période ${period.startDateInString} gardée: $hasTransactions")
                hasTransactions
            }
            // ✅ CORRECTION: Tri décroissant des périodes par date
            .sortedByDescending { (period, _) ->
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val timestamp = dateFormat.parse(period.startDateInString)?.time ?: 0L
                    Log.d("TransactionFilter", "Période ${period.startDateInString} timestamp: $timestamp")
                    timestamp
                } catch (e: Exception) {
                    Log.e("TransactionFilter", "Erreur parsing date ${period.startDateInString}: ${e.message}")
                    0L
                }
            }

        Log.d("TransactionFilter", "=== RÉSULTAT FINAL APRÈS TRI ===")
        Log.d("TransactionFilter", "Nombre de périodes avec transactions: ${result.size}")
        result.forEach { (period, transactions) ->
            Log.d("TransactionFilter", "Période finale: ${period.startDateInString} avec ${transactions.size} transactions")
            transactions.forEach { transaction ->
                Log.d("TransactionFilter", "  Transaction VID: ${transaction.vid}, Timestamp: ${transaction.timestamps}")
            }
        }

        result
    }

    Log.d("TransactionFilter", "=== PASSAGE À L'AFFICHAGE ===")
    MainList(filteredGroupedTransactions, dateStringName, viewModel, uiState, idClient)
}
