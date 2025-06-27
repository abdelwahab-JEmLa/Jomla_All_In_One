package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.TransactionCommercial
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

open class ClientsMapFilterViewModel(
    val repo_0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3? = null,
) : ViewModel() {
    protected val allListrepo_0_0_HeadSQLRepositorys = repo_0_0_HeadSQLRepositorys
        ?.repositorys_Model
        ?.c3TransactionCommercialRepository
        ?.modelDatasSnapList

    private var allStrNomJourEtSonSemainToStartJourTimeTemp by
    mutableStateOf<List<StrNomJourEtSonSemainToStartJourTimeTemp>>(
        emptyList()
    )
    data class StrNomJourEtSonSemainToStartJourTimeTemp
        (
        val vid: Long = 0,
        val nomJourArabe: String,
        val estDonLaSemainDistantDe: Int,
        val jourEstEntreTimeTemp: Pair<Long, Long>,
        val key: String = "vid->estDonLaSemainDistant(nomJourArabe)",
    )

    private var datesHistoriqueTransactions by
    mutableStateOf(DatesHistoriqueTransactions())

    init {
        viewModelScope.launch {
            collecteAddAuStrNomJourEtSonSemainToStartJourTimeTemp()
            collecteAddAuDatesHistoriqueTransactions()
        }
    }

    private fun collecteAddAuStrNomJourEtSonSemainToStartJourTimeTemp() {
        // Track changes in transactions with COMMANDE_LIVRAI status
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis

        // Create add list to store unique days with transactions
        val uniqueDays = mutableListOf<StrNomJourEtSonSemainToStartJourTimeTemp>()

        allListrepo_0_0_HeadSQLRepositorys?.forEach { transaction ->
            if (transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI) {
                // Get transaction date
                val transactionDate = Date(transaction.timestamps)
                val cal = Calendar.getInstance()
                cal.time = transactionDate

                // Get start and end of day
                val startDay = getStartOfDay(transaction.timestamps)
                val endDay = getEndOfDay(transaction.timestamps)

                // Calculate how many weeks ago this was
                val weeksDifference = getWeeksDifference(today, transaction.timestamps)

                // Get day name in Arabic
                val dayFormat = SimpleDateFormat("EEEE", Locale("ar"))
                val dayName = dayFormat.format(transactionDate)

                // Create key for uniqueness
                val key = "${startDay}_${dayName}_${weeksDifference}"

                // Check if this day is already in our list
                val existingDay = uniqueDays.find { it.key == key }

                if (existingDay == null) {
                    // Add new day if not exists
                    uniqueDays.add(
                        StrNomJourEtSonSemainToStartJourTimeTemp(
                            vid = transaction.vid,
                            nomJourArabe = dayName,
                            estDonLaSemainDistantDe = weeksDifference,
                            jourEstEntreTimeTemp = Pair(startDay, endDay),
                            key = key
                        )
                    )
                }
            }
        }

        // Update the state with the new list
        allStrNomJourEtSonSemainToStartJourTimeTemp = uniqueDays
    }

    private fun collecteAddAuDatesHistoriqueTransactions() {
        // Create instances for weeks and days based on the collected dates
        val semainsList = mutableListOf<DatesHistoriqueTransactions.Semain>()

        // Group by week distance
        val groupedByWeek = allStrNomJourEtSonSemainToStartJourTimeTemp.groupBy {
            it.estDonLaSemainDistantDe }

        groupedByWeek.forEach { (weekDistance, days) ->
            val semain = DatesHistoriqueTransactions.Semain()
            semain.vid = weekDistance.toLong()
            semain.key = "Semaine-$weekDistance"

            val joursList = mutableListOf<DatesHistoriqueTransactions.Semain.Jour>()

            days.forEach { dayInfo ->
                val jour = DatesHistoriqueTransactions.Semain.Jour()
                jour.vid = dayInfo.vid
                jour.key = dayInfo.key

                // Find all transactions for this day
                val dayTransactions = allListrepo_0_0_HeadSQLRepositorys?.filter { transaction ->
                    transaction.timestamps >= dayInfo.jourEstEntreTimeTemp.first &&
                            transaction.timestamps <= dayInfo.jourEstEntreTimeTemp.second
                } ?: emptyList()

                jour.cesCommercialTransactions = dayTransactions
                joursList.add(jour)
            }

            semain.cesJours = joursList
            semainsList.add(semain)
        }

        datesHistoriqueTransactions.cesSemains = semainsList
    }

    // Helper functions
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    private fun getWeeksDifference(current: Long, past: Long): Int {
        val diff = current - past
        return (diff / (7 * 24 * 60 * 60 * 1000)).toInt()
    }

    class DatesHistoriqueTransactions {
        var cesSemains by mutableStateOf<List<Semain>>(emptyList())

        class Semain {
            var vid by mutableStateOf(0L)
            var key by mutableStateOf("")
            var cActive by mutableStateOf(false)

            var cesJours by mutableStateOf<List<Jour>>(emptyList())

            class Jour {
                var vid by mutableStateOf(0L)
                var key by mutableStateOf("")
                var cActive by mutableStateOf(false)

                var cesCommercialTransactions by mutableStateOf<List<TransactionCommercial>>(emptyList())
            }
        }
    }

    private var currentFilter = FilterType.ALL

    // Définir un nouveau filtre
    open fun setFilter(filter: FilterType) {
        currentFilter = filter
    }

    private var visibleJourToListTransactionsLivre by
    mutableStateOf<List<StrNomJourEtSonSemainToStartJourTimeTemp>>(
        emptyList()
    )

    // Helper function to get filtered transactions based on current filter
    open fun getFilteredTransactions(): List<TransactionCommercial> {
        return when (currentFilter) {
            FilterType.ALL -> allListrepo_0_0_HeadSQLRepositorys ?: emptyList()
            FilterType.DatesHistoriqueTransactions -> {
                // Flatten the nested structure to get all transactions from active days/weeks
                datesHistoriqueTransactions.cesSemains
                    .filter { it.cActive }
                    .flatMap { semain ->
                        semain.cesJours
                            .filter { jour -> jour.cActive }
                            .flatMap { jour -> jour.cesCommercialTransactions }
                    }
            }
            FilterType.CIBLE -> {
                allListrepo_0_0_HeadSQLRepositorys?.filter { transaction ->
                    transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.Cible ||
                            transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.CIBLE_PRIORITE_2 ||
                            transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.CIBLE_PRIORITE_3 ||
                            transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.CIBLE_POUR_2
                } ?: emptyList()
            }
        }
    }

    enum class FilterType {
        ALL,
        DatesHistoriqueTransactions,
        CIBLE,
    }
}
