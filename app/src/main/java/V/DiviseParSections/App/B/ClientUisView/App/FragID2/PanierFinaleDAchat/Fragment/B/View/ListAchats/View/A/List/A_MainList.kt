package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.B_ProductGroup.View_Vent_M1Produit
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MainList_Frag_Panie(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3= koinViewModel(),
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainGetter: RepositorysMainGetter = viewModel.aCentralFacade.repositorysMainGetter,
    fVentCouleurOperationRepository: Repo10OperationVentCouleur = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur,
    its_From_SearchPrd: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()

    val groupedVents by remember {
        derivedStateOf {
            val filteredData = if (uiState.filterNonTrouve) {
                fVentCouleurOperationRepository.onVentFilteredDatas
                    .filter { it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve }
            } else {
                fVentCouleurOperationRepository.onVentFilteredDatas
            }

            // Group by product and sort each group by creation date descending
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

    if (its_From_SearchPrd) {
        var filteredVents by remember { mutableStateOf(groupedVents) }

        Column(
            modifier = modifier.padding(petitePaddine),
            verticalArrangement = Arrangement.spacedBy(petitePaddine)
        ) {
            Search_Prd(
                groupedVents = groupedVents,
                viewModel = viewModel
            ) { filteredResults ->
                filteredVents = filteredResults
            }

            filteredVents.entries.forEach { (productKeyId, achatGroup) ->
                View_Vent_M1Produit(
                    viewModel = viewModel,
                    productKeyId = productKeyId,
                    relative_List_M10OperationVentCouleur = achatGroup
                )
            }
        }
    } else {
        // Fix: Check if groupedVents is not empty before accessing first()
        if (groupedVents.isNotEmpty()) {
            val key = groupedVents.entries.toList().first().key
            val relative_FirstVent_Produit = repositorysMainGetter.find_M1Produit_ByKeyID(key)

            LazyColumn(
                modifier = modifier
                    .getSemanticsTag(relative_FirstVent_Produit, "relative_FirstVent_Produit")
                    .getSemanticsTag(groupedVents.entries.toList(), "")
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(groupedVents.entries.toList()) { (productKeyId, achatGroup) ->
                    View_Vent_M1Produit(
                        viewModel = viewModel,
                        productKeyId = productKeyId,
                        relative_List_M10OperationVentCouleur = achatGroup
                    )
                }
            }
        } else {
            // Handle empty state - show appropriate UI
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Aucun produit disponible",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun Search_Prd(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    groupedVents: Map<String, List<M10OperationVentCouleur>>,
    viewModel: ZViewModel_Sec1Frag3,
    onFilterChanged: (Map<String, List<M10OperationVentCouleur>>) -> Unit
) {
    var searchText by rememberSaveable { mutableStateOf("") }

    val bProduitDataBase_SubClassFunctionality =
        aCentralFacade.repositorysMainGetter.repo1ProduitInfos

    val filteredResults by remember {
        derivedStateOf {
            if (searchText.isBlank()) {
                groupedVents
            } else {
                val filtered = groupedVents.filter { (productKeyId, _) ->
                    val product = bProduitDataBase_SubClassFunctionality.datasValue
                        .find { it.keyID == productKeyId }

                    val productName = product?.nom?.takeIf { it.isNotBlank() }
                        ?: product?.nomMutable?.takeIf { it.isNotBlank() }
                        ?: "Product #$productKeyId"

                    productName.contains(searchText, ignoreCase = true)
                }

                // Maintain sorting even after filtering
                filtered.toList()
                    .sortedByDescending { (_, ventList) ->
                        ventList.maxOfOrNull { it.creationTimestamps } ?: 0L
                    }
                    .toMap()
            }
        }
    }

    // Update filtered results when they change
    onFilterChanged(filteredResults)

    OutlinedTextField(
        value = searchText,
        onValueChange = { searchText = it },
        modifier = modifier.fillMaxWidth(),
        label = { Text("Rechercher par nom de produit") },
        placeholder = { Text("Tapez le nom du produit...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(
                    onClick = { searchText = "" }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true
    )
}
