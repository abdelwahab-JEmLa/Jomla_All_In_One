package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_4_PeriodeVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.C3_BonAchate
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.SecID5FragID2UiState
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

// 🔧 CORRECTION 2: Dans MainList - Assurer le tri dans l'affichage
@Composable
fun MainList(
    filteredGroupedTransactions: List<Pair<_1_4_PeriodeVent, List<C3_BonAchate>>>,
    dateStringName: DatesHandler,
    viewModel: ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient,
    uiState: SecID5FragID2UiState,
    idClient: Long,
) {
    Log.d("TransactionList", "=== DÉBUT DE L'AFFICHAGE MainList ===")
    Log.d("TransactionList", "Nombre de périodes à afficher: ${filteredGroupedTransactions.size}")

    // ✅ VÉRIFICATION DU TRI: Log des transactions dans l'ordre d'affichage
    filteredGroupedTransactions.forEachIndexed { periodIndex, (period, transactions) ->
        Log.d("TransactionList", "Période $periodIndex: ${period.startDateInString}")
        transactions.forEachIndexed { transIndex, transaction ->
            Log.d("TransactionList", "  Position d'affichage $transIndex: VID=${transaction.vid}, Timestamp=${transaction.timestamps}")
        }
    }

    // Groupement par semaine
    val groupedByWeek = remember(filteredGroupedTransactions) {
        Log.d("TransactionList", "=== CRÉATION DES GROUPES PAR SEMAINE ===")

        filteredGroupedTransactions.groupBy { (period, _) ->
            try {
                val distanceWeek = dateStringName.getDistanceSemainParDateStr(period.startDateInString)
                Log.d("TransactionList", "Période ${period.startDateInString} → Semaine: '$distanceWeek'")

                if (distanceWeek == "قبل 3 أسابيع" || distanceWeek.isEmpty()) {
                    "الأسبوع من ${period.startDateInString}"
                } else {
                    distanceWeek
                }
            } catch (e: Exception) {
                Log.e("TransactionList", "Erreur calcul semaine: ${e.message}")
                "الأسبوع من ${period.startDateInString}"
            }
        }
    }

    // Tri des semaines par date la plus récente
    val sortedWeeks = remember(groupedByWeek) {
        groupedByWeek.toList().sortedByDescending { (_, periodsInWeek) ->
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val latestPeriod = periodsInWeek.maxByOrNull { (period, _) ->
                    dateFormat.parse(period.startDateInString)?.time ?: 0L
                }
                latestPeriod?.first?.let { period ->
                    dateFormat.parse(period.startDateInString)?.time ?: 0L
                } ?: 0L
            } catch (e: Exception) {
                0L
            }
        }
    }

    // Interface utilisateur
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (filteredGroupedTransactions.isNotEmpty()) {
                Log.d("TransactionList", "=== DÉBUT DE L'AFFICHAGE DES SEMAINES ===")

                sortedWeeks.forEach { (weekString, periodsInWeek) ->
                    Log.d("TransactionList", "=== AFFICHAGE SEMAINE: '$weekString' ===")

                    // En-tête de semaine
                    C_1_Header_WeekHeaderItem(weekString)

                    // ✅ CORRECTION: S'assurer que les périodes sont triées par date décroissante
                    val sortedPeriodsInWeek = periodsInWeek.sortedByDescending { (period, _) ->
                        try {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            dateFormat.parse(period.startDateInString)?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    }

                    sortedPeriodsInWeek.forEach { (period, transactions) ->
                        Log.d("TransactionList", "=== AFFICHAGE PÉRIODE: ${period.startDateInString} ===")

                        val dayName = dateStringName.getNomJourArabParDateStr(period.startDateInString)
                        val startTime = period.heurDebutInString
                        val endTime = period.endDateInString.ifEmpty { "الآن" }

                        // En-tête de période
                        C_2_Header_PeriodHeaderItem(
                            dayName = dayName,
                            startTime = startTime,
                            endTime = endTime
                        )

                        // ✅ VÉRIFICATION FINALE: Les transactions doivent déjà être triées
                        Log.d("TransactionList", "Vérification tri final des transactions:")
                        transactions.forEachIndexed { index, transaction ->
                            Log.d("TransactionList", "  Affichage final position $index: VID=${transaction.vid}, Timestamp=${transaction.timestamps}")
                        }

                        // Affichage des transactions (dans l'ordre trié)
                        transactions.forEach { transaction ->
                            B_Item_TransactionItem(
                                viewModel = viewModel,
                                transaction = transaction
                            )
                        }
                    }
                }
            } else {
                // Message si aucune transaction
                val messageText = if (uiState.transactionsDateToList_C_3_BonAchate.isNotEmpty())
                    "لا توجد معاملات للعميل $idClient"
                else "جاري تحميل البيانات..."

                Text(
                    text = messageText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
// 🔧 CORRECTION 3: Fonction utilitaire pour déboguer le tri
fun debugTransactionSorting(transactions: List<C3_BonAchate>, tag: String) {
    Log.d(tag, "=== DEBUG TRI DES TRANSACTIONS ===")
    transactions.forEachIndexed { index, transaction ->
        val dateTime = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = Date(transaction.timestamps)
            sdf.format(date)
        } catch (e: Exception) {
            "Invalid timestamp"
        }
        Log.d(tag, "Position $index: VID=${transaction.vid}, Timestamp=${transaction.timestamps} ($dateTime)")
    }
}
