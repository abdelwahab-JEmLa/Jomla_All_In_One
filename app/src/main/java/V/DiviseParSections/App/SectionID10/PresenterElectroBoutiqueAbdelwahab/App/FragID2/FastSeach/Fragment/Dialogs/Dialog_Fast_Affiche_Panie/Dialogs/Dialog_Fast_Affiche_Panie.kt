package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Dialog_Fast_Affiche_Panie(modifier: Modifier = Modifier) {
    // Dialog implementation can be added here
    MainList(modifier = modifier)
}

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade =  koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val active_Central_Values = focusedValuesGetter.active_Central_Values

    val groupedVents by remember {
        derivedStateOf {
            val allVents = focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent

            val filteredData = when (active_Central_Values.activeFilter) {
                is ActiveCentralValues.ActiveFilter.NonTrouve -> {
                    allVents.filter { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
                }
                is ActiveCentralValues.ActiveFilter.PrixAuGerant -> {
                    // Filter logic for PrixAuGerant can be implemented here
                    allVents
                }
                else -> allVents
            }

            filteredData
                .groupBy { it.parent_M1Produit_KeyId }
                .mapValues { (_, ventList) ->
                    ventList.sortedByDescending { it.creationTimestamps }
                }
                // Sort the groups themselves by the latest creation date in each group
                .toList()
                .sortedByDescending { (_, ventList) ->
                    ventList.maxOfOrNull { it.creationTimestamps } ?: 0L
                }
                .toMap()
        }
    }

    // Fixed: Implemented LazyVerticalGrid with 2 columns containing Produit_Vent
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(groupedVents.toList()) { (produitKeyId, ventList) ->
            Produit_Vent(
                produitKeyId = produitKeyId,
                ventList = ventList,
                aCentralFacade = aCentralFacade,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun Produit_Vent(
    produitKeyId: String,
    ventList: List<M10OperationVentCouleur>,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    modifier: Modifier = Modifier
) {
    val produit = remember(produitKeyId) {
        repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Product name
            Text(
                text = produit?.nom ?: "Produit inconnu",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Show total quantity
            val totalQuantity = ventList.sumOf { it.quantity }
            Text(
                text = "Quantité totale: $totalQuantity",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Show number of operations
            Text(
                text = "${ventList.size} opération(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Show latest operation details if available
            ventList.firstOrNull()?.let { latestVent ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "État: ${latestVent.etateActuellementEst}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
