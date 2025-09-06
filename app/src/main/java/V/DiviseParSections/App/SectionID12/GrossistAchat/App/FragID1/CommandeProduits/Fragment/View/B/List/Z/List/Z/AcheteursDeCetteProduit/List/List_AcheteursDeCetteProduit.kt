package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List.View.Parent_Dispo_Vent_StateFull
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun List_AcheteursDeCetteProduit(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
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
    val clientsList = relative_Map_M2Client_To_ListM10Vent.entries.toList()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 150.dp)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .getSemanticsTag(
                    nomVal = "listFCouleurVentOperation",
                    data = relative_ListM10VentOperation
                )
                .fillMaxWidth()
        ) {
            if (clientsList.isEmpty()) {
                item {
                    Text(
                        text = "Aucun client trouvé pour ce produit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(clientsList) { (relative_M2Client, relative_ListM10Vent) ->
                    if (relative_M2Client != null) {
                        val client = viewModel.getter.repo2Client.datasValue.find {
                            it.keyID == relative_M2Client.keyID
                        }

                        if (client != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
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

                                        val size_vents_pour_bon = repo10OperationVentCouleur.datasValue.filter { ventOperation ->
                                            val produitForThisVent = repositorysMainGetter.find_M1Produit_ByKeyID(ventOperation.parent_M1Produit_KeyId)
                                            ventOperation.parent_M8BonVent_KeyId == relative_M10Vent.parent_M8BonVent_KeyId &&
                                                    produitForThisVent?.its_Carton != true  // Exclude carton products
                                        }.size

                                        when (relative_M10Vent.its_Linked_To_Autre_Vent_Si_NonDispo) {
                                            false -> {
                                                VentOperationItem(
                                                    size_vents_pour_bon=size_vents_pour_bon,
                                                    relative_M10Vent = relative_M10Vent,
                                                    repositorysMainSetter = repositorysMainSetter,
                                                    viewModel = viewModel
                                                )
                                            }

                                            true -> Parent_Dispo_Vent_StateFull(

                                                relative_M10Vent = relative_M10Vent,
                                                relative_M2Client = relative_M2Client,
                                                relative_M1Produit = repositorysMainGetter.find_M1Produit_ByKeyID(
                                                    relative_M10Vent.parent_M1Produit_KeyId
                                                )
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
}

@Composable
private fun VentOperationItem(
    relative_M10Vent: M10OperationVentCouleur,
    repositorysMainSetter: RepositorysMainSetter,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    size_vents_pour_bon: Int,
    modifier: Modifier = Modifier
) {
    val isNotFound = relative_M10Vent.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve
    val backgroundColor = if (isNotFound) Color.Black else Color.Transparent
    val textColor = if (isNotFound) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    val tariffInfo = viewModel.aCentralFacade.repositorysMainGetter
        .find_M13Tarification_By_KeyID(relative_M10Vent.parentM13TarificationKeyID)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Toggle button for EtateDelivery (always full opacity)
            Button(
                onClick = {
                    val updatedVentOperation = relative_M10Vent.copy(
                        etateDelivery = when (relative_M10Vent.etateDelivery) {
                            M10OperationVentCouleur.EtateDelivery.Trouve -> M10OperationVentCouleur.EtateDelivery.NonTrouve
                            M10OperationVentCouleur.EtateDelivery.NonTrouve -> M10OperationVentCouleur.EtateDelivery.Trouve
                        }
                    )
                    repositorysMainSetter.updateListM10OperationVentCouleur(
                        buildList { add(updatedVentOperation) }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (relative_M10Vent.etateDelivery) {
                        M10OperationVentCouleur.EtateDelivery.Trouve -> MaterialTheme.colorScheme.primary
                        M10OperationVentCouleur.EtateDelivery.NonTrouve -> MaterialTheme.colorScheme.error
                    }
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = when (relative_M10Vent.etateDelivery) {
                        M10OperationVentCouleur.EtateDelivery.Trouve -> "Trouvé"
                        M10OperationVentCouleur.EtateDelivery.NonTrouve -> "Non Trouvé"
                    },
                    fontSize = 12.sp
                )
            }

            Column {
                Text(
                    text = "• Qté: ${relative_M10Vent.quantity}",
                    fontSize = 14.sp,
                    color = textColor
                )

                // Display tariff price for client in card with tariff color
                tariffInfo?.let { tariff ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = tariff.typeChoisi.couleur
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "Prix: ${tariff.prixCurrency} (${tariff.typeChoisi.nomArabe})",
                            fontSize = 12.sp,
                            color = tariff.typeChoisi.couleur_Text,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Floating Action Button for sales count - positioned at top-end
        FloatingActionButton(
            onClick = { /* Add your click action here if needed */ },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(36.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Text(
                text = "$size_vents_pour_bon",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
