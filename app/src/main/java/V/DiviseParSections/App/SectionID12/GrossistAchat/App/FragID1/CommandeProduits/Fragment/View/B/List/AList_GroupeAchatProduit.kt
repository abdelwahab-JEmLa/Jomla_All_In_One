package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.W_AchatProduitOperation.View.View_AchatProduitOperation
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun List_GroupeAchatProduit(
    modifier: Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
) {
    val repo = viewModel.getter.repo11AchatOperation

    // Use the filtered data from the repository instead of raw data
    // This ensures that grossist filtering works properly
    val filteredData = repo.filteredDatasValue

    // Group the operations by product with enhanced error handling
    val items = remember(filteredData) {
        try {
            filteredData.mapNotNull { achatOperation ->
                try {
                    // Additional validation before processing
                    if (achatOperation.parent_M3CouleurProduit_KeyID.isBlank() ||
                        achatOperation.parent_M3CouleurProduit_KeyID == "null") {
                        Log.w("List_GroupeAchatProduit", "Skipping operation with invalid couleur key: ${achatOperation.keyID.takeLast(3)}")
                        return@mapNotNull null
                    }

                    val relatedSalesOperations = achatOperation.get_list_v_Depuit_joinedStringKeys(
                        repo.repo10OperationVentCouleur.datasValue
                    )

                    if (relatedSalesOperations.isNotEmpty()) {
                        val salesOperation = relatedSalesOperations.firstOrNull()
                        val produitKeyId = salesOperation?.parentM1ProduitInfosKeyId

                        if (!produitKeyId.isNullOrBlank() && produitKeyId != "null" && produitKeyId.length > 5) {
                            produitKeyId to achatOperation
                        } else {
                            Log.w("List_GroupeAchatProduit", "Skipping operation with invalid produit key: '$produitKeyId' for operation ${achatOperation.keyID.takeLast(3)}")
                            null
                        }
                    } else {
                        Log.w("List_GroupeAchatProduit", "No related sales operations for achat operation ${achatOperation.keyID.takeLast(3)}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("List_GroupeAchatProduit", "Error processing achat operation ${achatOperation.keyID.takeLast(3)}: ${e.message}", e)
                    null
                }
            }.groupBy({ it.first }, { it.second }).entries.toList()
        } catch (e: Exception) {
            Log.e("List_GroupeAchatProduit", "Error grouping operations: ${e.message}", e)
            emptyList()
        }
    }

    // Enhanced logging with more context
    Log.d("List_GroupeAchatProduit", buildString {
        append("Processing state: ")
        append("Raw data: ${repo.datasValue.size}, ")
        append("Filtered: ${filteredData.size}, ")
        append("Grouped items: ${items.size}")

        if (filteredData.size != repo.datasValue.size) {
            append(" [FILTER ACTIVE]")
        }
    })

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Column {
            if (items.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier
                        .getSemanticsTag(
                            data = repo.datasValue,
                            nomVal = "repo11AchatOperation_datasValue"
                        )
                        .getSemanticsTag(
                            data = items,
                            nomVal = "grouped_items_list"
                        )
                        .fillMaxWidth()
                        .padding(petitePaddine),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = buildString {
                            when {
                                repo.datasValue.isEmpty() -> {
                                    append("Aucune opération d'achat disponible")
                                    append("\nAjoutez des opérations d'achat pour commencer")
                                }
                                filteredData.isEmpty() -> {
                                    append("Aucune opération d'achat ne correspond au filtre actuel")
                                    append("\n(${repo.datasValue.size} opérations totales disponibles)")
                                    append("\nModifiez ou supprimez le filtre pour voir plus d'opérations")
                                }
                                else -> {
                                    append("Aucune opération d'achat valide trouvée")
                                    append("\n(${filteredData.size} opérations après filtrage)")
                                    append("\nVérifiez la validité des données ou contactez le support")
                                }
                            }
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn {
                    items(items) { groupeAchatProduit ->
                        View_AchatProduitOperation(
                            viewModel = viewModel,
                            groupeAchatProduit = groupeAchatProduit
                        )
                    }
                }
            }
        }
    }
}
