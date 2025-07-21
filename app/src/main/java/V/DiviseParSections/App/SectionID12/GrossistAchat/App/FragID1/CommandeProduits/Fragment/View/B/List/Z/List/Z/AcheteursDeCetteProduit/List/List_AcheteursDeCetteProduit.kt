package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List.View.Parent_Dispo_Vent_StateFull
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun List_AcheteursDeCetteProduit(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repo10OperationVentCouleur: Repo10OperationVentCouleur = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur,
    relative_M11AchatOperation: M11AchatOperation
) {
    val relative_ListM10VentOperation =
        relative_M11AchatOperation.get_list_v_Depuit_joinedStringKeys(
            repo10OperationVentCouleur.datasValue
        )

    val relative_Map_M2Client_To_ListM10Vent =
        relative_ListM10VentOperation.groupBy { ventOperation ->
            val gBonVent = viewModel.getter.repo8BonVent.datasValue.find {
                it.keyID == ventOperation.parent_M8BonVent_KeyId
            }
            gBonVent?.parent_M2Client_KeyID?.let { repositorysMainGetter.find_M2Client(it) }
        }.filterKeys { it != null }

    Column(
        modifier = Modifier
            .getSemanticsTag(
                nomVal = "listFCouleurVentOperation",
                data = relative_ListM10VentOperation
            )
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        if (relative_Map_M2Client_To_ListM10Vent.isEmpty()) {
            Text(
                text = "Aucun client trouvé pour ce produit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            relative_Map_M2Client_To_ListM10Vent.forEach { (relative_M2Client, relative_ListM10Vent) ->

                if (relative_M2Client != null) {
                    val client = viewModel.getter.repo2Client.datasValue.find {
                        it.keyID == relative_M2Client.keyID
                    }

                    if (client != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = client.nom,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                relative_ListM10Vent.forEach { relative_M10Vent ->
                                    when (relative_M10Vent.its_Linked_To_Autre_Vent_Si_NonDispo) {
                                        false -> {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "• Qté: ${relative_M10Vent.quantity}",
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )

                                                Spacer(modifier = Modifier.width(16.dp))

                                                val bonVent =
                                                    viewModel.getter.repo8BonVent.datasValue.find {
                                                        it.keyID == relative_M10Vent.parent_M8BonVent_KeyId
                                                    }
                                                bonVent?.let {
                                                    Text(
                                                        text = "Bon: ${it.keyID.takeLast(6)}",
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                            alpha = 0.7f
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        true -> Parent_Dispo_Vent_StateFull(
                                            relative_M10Vent=relative_M10Vent,
                                            relative_M2Client = relative_M2Client,
                                            relative_M1Produit = repositorysMainGetter.find_M1Produit(relative_M10Vent.parent_M1Produit_KeyId)
                                        )
                                    }
                                }

                                val totalQuantity = relative_ListM10Vent.sumOf { it.quantity }
                                if (relative_ListM10Vent.size > 1) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Total: $totalQuantity",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
