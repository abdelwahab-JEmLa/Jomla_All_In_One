package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.W_AchatProduitOperation.View.View_AchatProduitOperation
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.filters_Central.filterAchatOperations
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun List_GroupeAchatProduit(
    modifier: Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    headViewModel: HeadViewModel = koinViewModel(),
) {
   val outlined_filter_searcher_achat=  focusedValuesGetter.active_Central_Values.outlined_filter_searcher_achat
    val repo = aCentralFacade.repositorysMainGetter.repo11AchatOperation
    val repo10OperationVentCouleur = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur

    val filteredAchatOperations by remember {
        derivedStateOf {
            filterAchatOperations(
                aCentralFacade = aCentralFacade,
            )
        }
    }

    val items = remember(filteredAchatOperations) {  //<--
    //TODO(1): ajout un filtre apre que les autre son fait que suit outlined_filter_searcher_achat par nom du produit
        filteredAchatOperations.mapNotNull { achat ->
            if (achat.parent_M3CouleurProduit_KeyID.isBlank() || achat.parent_M3CouleurProduit_KeyID == "null") {
                return@mapNotNull null
            }
            val sales =
                achat.get_list_v_Depuit_joinedStringKeys(repo10OperationVentCouleur.datasValue)
            val produitId = sales.firstOrNull()?.parent_M1Produit_KeyId
            if (produitId.isNullOrBlank() || produitId == "null" || produitId.length <= 5) {
                return@mapNotNull null
            }
            produitId to achat
        }.groupBy({ it.first }, { it.second }).entries.toList()
    }

    val listState = rememberLazyListState()
    val uiState by headViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.productDisplayController.mainGridScrollPosition) {
        val targetPosition = uiState.productDisplayController.mainGridScrollPosition
        if (!uiState.productDisplayController.isHostPhone &&
            uiState.productDisplayController.isConnected &&
            targetPosition >= 0 &&
            targetPosition < items.size
        ) {
            scope.launch {
                try {
                    listState.animateScrollToItem(index = targetPosition, scrollOffset = 0)
                } catch (e: Exception) {
                    try {
                        listState.scrollToItem(targetPosition)
                    } catch (scrollException: Exception) {
                    }
                }
            }
        }
    }

    LaunchedEffect(listState) {
        if (uiState.productDisplayController.isHostPhone &&
            uiState.productDisplayController.isConnected
        ) {
            snapshotFlow {
                listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
            }
                .distinctUntilChanged()
                .collect { (position, offset) ->
                    headViewModel.sendOrderToClientDisplayer(
                        WifiUpdateClientDisplayerStats.ClientMainGridScrollPosition.prefix,
                        position
                    )
                }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        if (items.isEmpty()) {
            ElevatedCard(
                modifier = Modifier
                    .getSemanticsTag(repo.datasValue, "repo11AchatOperation_datasValue")
                    .getSemanticsTag(filteredAchatOperations, "filtered_achat_operations")
                    .getSemanticsTag(items, "grouped_items_list")
                    .fillMaxWidth()
                    .padding(petitePaddine),
                elevation = CardDefaults.elevatedCardElevation(2.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = when {
                        repo.datasValue.isEmpty() ->
                            "Aucune opération d'achat disponible\nAjoutez des opérations d'achat pour commencer"

                        filteredAchatOperations.isEmpty() -> {
                            val activeFilters = mutableListOf<String>()
                            focusedValuesGetter.active_Central_Values.active_M14VentPeriode_AuFilterAchats?.let {
                                activeFilters.add("Période: ")
                            }
                            focusedValuesGetter.active_Central_Values.active_M15Grossist_AuFilterAchats?.let {
                                activeFilters.add("Grossiste: ${it.nom}")
                            }
                            focusedValuesGetter.active_Central_Values.active_M2Client_AuFilterAchats?.let {
                                activeFilters.add("Client: ${it.nom}")
                            }
                            focusedValuesGetter.active_Central_Values.active_M1Produit_AuFilterAchats?.let {
                                activeFilters.add("Produit: ${it.nom}")
                            }

                            "Aucune opération d'achat ne correspond aux filtres actifs:\n" +
                                    activeFilters.joinToString("\n") +
                                    "\n\n(${repo.datasValue.size} opérations totales disponibles)\n" +
                                    "Modifiez ou supprimez les filtres pour voir plus d'opérations"
                        }

                        else ->
                            "Aucune opération d'achat valide trouvée\n" +
                                    "(${filteredAchatOperations.size} opérations après filtrage)\n" +
                                    "Vérifiez la validité des données ou contactez le support"
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(items) { groupe ->
                    View_AchatProduitOperation(viewModel, groupe)
                }
            }
        }
    }
}
