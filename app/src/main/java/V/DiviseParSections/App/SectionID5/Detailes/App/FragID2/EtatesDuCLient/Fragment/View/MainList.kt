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

// 🔧 CORRECTION 3: Dans MainList.kt - Vérification finale du tri
// 🔧 CORRECTION 3: Dans MainList.kt - Simplification du tri des semaines
@Composable
fun MainList(
    filteredGroupedTransactions: List<Pair<_1_4_PeriodeVent, List<C3_BonAchate>>>,
    dateStringName: DatesHandler,
    viewModel: ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient,
    uiState: SecID5FragID2UiState,
    idClient: Long,
) {
    Log.d("MainList", "=== AFFICHAGE FINAL ===")
    Log.d("MainList", "Nombre de périodes à afficher: ${filteredGroupedTransactions.size}")

    // Vérification finale de l'ordre
    filteredGroupedTransactions.forEachIndexed { periodIndex, (period, transactions) ->
        Log.d("MainList", "Période $periodIndex: ${period.startDateInString}")
        transactions.forEachIndexed { transIndex, transaction ->
            Log.d("MainList", "  Affichage $transIndex: VID=${transaction.vid}, Timestamp=${transaction.timestamps}")
        }
    }

    // Groupement par semaine SANS re-tri
    val groupedByWeek = remember(filteredGroupedTransactions) {
        filteredGroupedTransactions.groupBy { (period, _) ->
            try {
                val distanceWeek = dateStringName.getDistanceSemainParDateStr(period.startDateInString)
                if (distanceWeek == "قبل 3 أسابيع" || distanceWeek.isEmpty()) {
                    "الأسبوع من ${period.startDateInString}"
                } else {
                    distanceWeek
                }
            } catch (e: Exception) {
                Log.e("MainList", "Erreur calcul semaine: ${e.message}")
                "الأسبوع من ${period.startDateInString}"
            }
        }
    }

    // Tri des semaines par date la plus récente
    val sortedWeeks = remember(groupedByWeek) {
        groupedByWeek.toList().sortedByDescending { (_, periodsInWeek) ->
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                periodsInWeek.maxOfOrNull { (period, _) ->
                    dateFormat.parse(period.startDateInString)?.time ?: 0L
                } ?: 0L
            } catch (e: Exception) {
                Log.e("MainList", "Erreur tri semaines: ${e.message}")
                0L
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (filteredGroupedTransactions.isNotEmpty()) {
            Log.d("MainList", "=== DÉBUT AFFICHAGE SEMAINES ===")

            sortedWeeks.forEach { (weekString, periodsInWeek) ->
                Log.d("MainList", "=== SEMAINE: '$weekString' ===")

                // En-tête de semaine
                C_1_Header_WeekHeaderItem(weekString)

                // ✅ CORRECTION: Tri des périodes dans chaque semaine par date décroissante
                val sortedPeriodsInWeek = periodsInWeek.sortedByDescending { (period, _) ->
                    try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .parse(period.startDateInString)?.time ?: 0L
                    } catch (e: Exception) {
                        0L
                    }
                }

                sortedPeriodsInWeek.forEach { (period, transactions) ->
                    Log.d("MainList", "=== PÉRIODE: ${period.startDateInString} ===")

                    val dayName = dateStringName.getNomJourArabParDateStr(period.startDateInString)
                    val startTime = period.heurDebutInString
                    val endTime = period.endDateInString.ifEmpty { "الآن" }

                    // En-tête de période
                    C_2_Header_PeriodHeaderItem(
                        dayName = dayName,
                        startTime = startTime,
                        endTime = endTime
                    )

                    // ✅ AFFICHAGE DES TRANSACTIONS dans l'ordre reçu du ViewModel
                    transactions.forEach { transaction ->
                        B_Item_TransactionItem(
                            viewModel = viewModel,
                            transaction = transaction
                        )
                    }
                }
            }
        } else {
            Text(
                text = if (uiState.transactionsDateToList_C_3_BonAchate.isNotEmpty())
                    "لا توجد معاملات للعميل $idClient"
                else "جاري تحميل البيانات...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
