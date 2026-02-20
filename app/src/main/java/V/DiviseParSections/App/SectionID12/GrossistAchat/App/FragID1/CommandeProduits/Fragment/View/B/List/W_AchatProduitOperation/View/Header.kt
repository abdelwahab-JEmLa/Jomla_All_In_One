package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.W_AchatProduitOperation.View

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import org.koin.compose.koinInject
import java.text.DecimalFormat

@Composable
fun Header(
    relative_produit: M01Produit,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    groupeAchatProduit: Map.Entry<String, List<M11AchatOperation>>
) {
    var showDialog by remember { mutableStateOf(false) }
    val list_M11AchatOperation = groupeAchatProduit.value

    val firstAchatOperation = groupeAchatProduit.value.firstOrNull()

    val priceFormatter = DecimalFormat("#,##0.00")
    val formattedPrixAchat =
        priceFormatter.format(firstAchatOperation?.prix_Achat_De_Cette_Grossist)

    val grossist = firstAchatOperation?.let { achat ->
        viewModel.aCentralFacade.repositorysMainGetter.repo15Grossist.datasValue
            .find { it.keyID == achat.parent_M15Grossist_KeyID }
    }

    Card(
        modifier = Modifier
            .getSemanticsTag(relative_produit,"produit")
            .getSemanticsTag(list_M11AchatOperation,"list_M11AchatOperation")
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First Row: Product name and purchase info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product info column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = relative_produit.nom,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Achat: $formattedPrixAchat DA",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Second Row: Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card_StatueDuProduit(
                    relative_Produit = relative_produit
                )

                Button(
                    modifier = Modifier
                        .getSemanticsTag(list_M11AchatOperation, "list_M11AchatOperation")
                        .getSemanticsTag_By_datas_A_Affiche_Au_Nom(list_M11AchatOperation.map { it.parent_M15Grossist_DebugInfos }
                            , "list_M11AchatOperation")
                        .getSemanticsTag(grossist, "grossist"),
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (grossist != null) {
                            try {
                                Color(grossist.couleur_In_Str.toColorInt())
                            } catch (e: Exception) {
                                MaterialTheme.colorScheme.primary
                            }
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                ) {
                    Text(
                        text = grossist?.nom ?: "Choisir Grossiste",
                        color = Color.White
                    )
                }
            }
        }
    }

    if (showDialog) {
        Dialog_Choisire_Grossist_Pour_AChat(
            viewModel = viewModel,
            list_M11AchatOperation = list_M11AchatOperation,
        ) { grossistSelected ->
            if (grossistSelected != null) {
                val datas = updated_Achats(list_M11AchatOperation, grossistSelected)
                datas.forEach { achatOperation ->
                    viewModel.aCentralFacade.repositorysMainSetter.repo11AchatOperation_update_If_Exist(
                        achatOperation
                    )
                }
            }
            showDialog = false
        }
    }
}
fun updated_Achats(
    list_M11AchatOperation: List<M11AchatOperation>,
    grossistSelected: M15Grossist
) = list_M11AchatOperation.map {
    it.copy(
        parent_M15Grossist_DebugInfos = grossistSelected.get_DebugInfos(),
        parent_M15Grossist_KeyID = grossistSelected.keyID
    )
}

@Composable
fun Card_StatueDuProduit(
    relative_Produit: M01Produit,
    repositorysMainSetter: RepositorysMainSetter = koinInject()
) {
    fun update_produit(produit: M01Produit): Unit {
        repositorysMainSetter.upsert_M1Produit(
            produit
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp), // Reduced from 8.dp
            modifier = Modifier.padding(6.dp) // Reduced from 8.dp
        ) {
            Text(
                text = "Carton:",
                style = MaterialTheme.typography.labelSmall, // Changed from labelMedium
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )

            Switch(
                checked = relative_Produit.its_Carton,
                onCheckedChange = { isChecked ->
                    val updatedProduit = relative_Produit.copy(
                        its_Carton = isChecked,
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                    update_produit(updatedProduit)
                },
                modifier = Modifier
                    .scale(0.8f), // Scale down the switch to make it smaller
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
