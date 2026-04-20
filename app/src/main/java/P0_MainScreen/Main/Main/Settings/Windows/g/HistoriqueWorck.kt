package P0_MainScreen.Main.Main.Settings.Windows.g

import P0_MainScreen.Main.Main.Settings.Windows.EnhancedBonVentCard
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun HistoriqueWorck(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    modifier: Modifier = Modifier
) {
    val currentActiveFocuced_M14VentPeriode = focusedValuesGetter.currentActiveFocuced_M14VentPeriode
    val bons_De_Cette_Period = repositorysMainGetter.repo8BonVent.datasValue.filter {
        it.parent_M14VentPeriod_KeyId == (currentActiveFocuced_M14VentPeriode?.keyID ?: "")
    }

    HistoriqueWorckContent(
        bons_De_Cette_Period = bons_De_Cette_Period,
        repositorysMainGetter = repositorysMainGetter,
        currentPeriod = currentActiveFocuced_M14VentPeriode,
        modifier = modifier
    )
}

@Composable
fun HistoriqueWorckContent(
    bons_De_Cette_Period: List<M8BonVent>,
    repositorysMainGetter: RepositorysMainGetter,
    currentPeriod: M14VentPeriode?,
    modifier: Modifier = Modifier
) {
    // State for filtering
    var selectedStatusFilter by remember { mutableStateOf<M8BonVent.EtateActuellementEst?>(null) }

    // Apply status filter if selected
    val filteredBons = if (selectedStatusFilter != null) {
        bons_De_Cette_Period.filter { it.etateActuellementEst == selectedStatusFilter }
    } else {
        bons_De_Cette_Period
    }

    // Sort orders: confirmed orders (with confirmation time > 0) at top, others below
    val sortedBons = filteredBons.sortedWith(
        compareByDescending<M8BonVent> { it.confirmeCommande_TimeTamp > 0 }
            .thenByDescending {
                if (it.confirmeCommande_TimeTamp > 0) it.confirmeCommande_TimeTamp
                else it.creationTimestamps
            }
    )

    // Separate confirmed and unconfirmed orders for section headers
    val confirmedOrders = sortedBons.filter { it.confirmeCommande_TimeTamp > 0 }
    val unconfirmedOrders = sortedBons.filter { it.confirmeCommande_TimeTamp <= 0 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        // Header statistics card
        item {
            PeriodStatisticsCard(
                bons = bons_De_Cette_Period,
                currentPeriod = currentPeriod
            )
        }

        // Quick stats row with filtering functionality
        item {
            QuickStatsRowWithFilter(
                bons = bons_De_Cette_Period,
                selectedFilter = selectedStatusFilter,
                onFilterSelected = { status ->
                    selectedStatusFilter = if (selectedStatusFilter == status) null else status
                }
            )
        }

        // Show active filter indicator
        if (selectedStatusFilter != null) {
            item {
                ActiveFilterIndicator(
                    selectedStatus = selectedStatusFilter!!,
                    onClearFilter = { selectedStatusFilter = null }
                )
            }
        }

        // Confirmed orders section with transition cards
        if (confirmedOrders.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "الطلبات المؤكدة",
                    count = confirmedOrders.size,
                    icon = Icons.Default.CheckCircle,
                    color = Color.Green
                )
            }

            itemsIndexed(
                items = confirmedOrders,
                key = { _, bon -> "confirmed_${bon.keyID}" }
            ) { index, bon ->
                // Add transition card between confirmed orders
                if (index > 0) {
                    val previousBon = confirmedOrders[index - 1]
                    TransitionCard(
                        previousBon = previousBon,
                        currentBon = bon
                    )
                }

                EnhancedBonVentCard(
                    bon = bon,
                    repositorysMainGetter = repositorysMainGetter
                )
            }
        }

        // Unconfirmed orders section
        if (unconfirmedOrders.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "الطلبات غير المؤكدة",
                    count = unconfirmedOrders.size,
                    icon = Icons.Default.Schedule,
                    color = Color.Red
                )
            }

            itemsIndexed(
                items = unconfirmedOrders.sortedByDescending { it.creationTimestamps },
                key = { _, bon -> "unconfirmed_${bon.keyID}" }
            ) { _, bon ->
                EnhancedBonVentCard(
                    bon = bon,
                    repositorysMainGetter = repositorysMainGetter
                )
            }
        }

        // Empty state (show when filtered results are empty or no orders at all)
        if (sortedBons.isEmpty()) {
            item {
                if (selectedStatusFilter != null) {
                    FilteredEmptyStateCard(selectedStatus = selectedStatusFilter!!)
                } else {
                    EmptyStateCard()
                }
            }
        }
    }
}
