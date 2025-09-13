package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.f

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFormatterUtils
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.f.z.Com.ElevatedCardHeader
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Produit_Vent(
    produitKeyId: String,
    ventList: List<M10OperationVentCouleur>,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    modifier: Modifier = Modifier
) {

    val produit = remember(produitKeyId) {
        repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
    }

    val hasNonTrouve = remember(ventList) {
        ventList.any { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
    }

    val allNonTrouve = remember(ventList) {
        ventList.isNotEmpty() && ventList.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
    }

    // Format first vent creation time
    val firstVentCreationTime = remember(ventList) {
        ventList.firstOrNull()?.let { firstVent ->
            val timestamp = if (firstVent.creationTimestamps > 0) {
                firstVent.creationTimestamps
            } else {
                firstVent.dernierTimeTampsSynchronisationAvecFireBase
            }

            if (timestamp > 0) {
                val sdf = SimpleDateFormat("HH:mm:ss a", Locale.getDefault())
                sdf.format(Date(timestamp))
            } else {
                null
            }
        }
    }

    val pdfFormatterUtils = remember { PdfFormatterUtils(repositorysMainGetter) }

    produit?.let { nonNullProduit ->
        Box(modifier = modifier) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (hasNonTrouve) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Card_Deplace_Hold_Up_To_This_Vent(produit = nonNullProduit)

                    // ElevatedCardHeader now includes the up icon functionality
                    ElevatedCardHeader(
                        produit = nonNullProduit,
                        hasNonTrouve = hasNonTrouve,
                        allNonTrouve = allNonTrouve,
                        ventList = ventList,
                        aCentralFacade = aCentralFacade
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = nonNullProduit.nom,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = nonNullProduit.position_store_3jamale.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Show first vent creation time
                    firstVentCreationTime?.let { timeString ->
                        Text(
                            text = "Première commande: $timeString",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(
                                alpha = 0.8f
                            )
                            else MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Show total quantity using PdfFormatterUtils.formatQuantity
                    val totalQuantity = ventList.sumOf { it.quantity }
                    val cartonSize = nonNullProduit.quantite_Boit_Par_Carton ?: 1
                    val formattedQuantity = pdfFormatterUtils.formatQuantity(
                        qty = totalQuantity,
                        cartonSize = cartonSize,
                        produit = nonNullProduit
                    )
                    Text(
                        text = "Qyt: $formattedQuantity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Show number of operations
                    Text(
                        text = "${ventList.size} couleur(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Add some bottom padding to accommodate the floating button
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Floating Toggle Button positioned at bottom-end of the card
            ToggleButton_PremierCheckDonne(
                ventList = ventList,
                onToggle = { newState ->
                    // Update all ventList items with the new premier_Check_Donne state
                    ventList.forEach { vent ->
                        repositorysMainSetter.upsert_M10OperationVentCouleur(
                            vent.copy(premier_Check_Donne = newState)
                        )
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    } ?: run {
        // Handle case where produit is null
        Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = "Produit non trouvé (ID: ${produitKeyId.take(8)}...)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ce produit n'existe plus dans la base de données",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun Card_Deplace_Hold_Up_To_This_Vent(
    produit: V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = koinInject()
) {
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val shouldShow = activeCentralValues.held_Produit_Pour_Move_Au_Position_Store != null

    if (shouldShow) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF9C27B0) // Purple color
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Déplacer le produit sélectionné vers le haut",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        activeCentralValues.held_Produit_Pour_Move_Au_Position_Store?.let { heldProduit ->
                            val currentPosition = heldProduit.position_store_3jamale ?: 0
                            val newPosition = if (currentPosition > 0) currentPosition - 1 else 0

                            repositorysMainSetter.upsert_M1Produit(
                                heldProduit.copy(
                                    position_store_3jamale = newPosition,
                                    dernier_timeTamps_position_store_3jamale = System.currentTimeMillis()
                                )
                            )

                            // Clear the held product after moving
                            focusedValuesGetter.update_activeCentralValues(
                                activeCentralValues.copy(
                                    held_Produit_Pour_Move_Au_Position_Store = null
                                )
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Déplacer vers le haut",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun ToggleButton_PremierCheckDonne(
    ventList: List<M10OperationVentCouleur>,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine current state: true if ALL items have premier_Check_Donne = true
    val allChecked = remember(ventList) {
        ventList.isNotEmpty() && ventList.all { it.premier_Check_Donne }
    }

    // Determine what the new state should be when toggled
    val newStateWhenToggled = !allChecked

    FloatingActionButton(
        onClick = { onToggle(newStateWhenToggled) },
        modifier = modifier
            .padding(16.dp)
            .size(48.dp),
        containerColor = if (allChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (allChecked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Icon(
            imageVector = if (allChecked) Icons.Filled.RadioButtonUnchecked else Icons.Filled.Check,
            contentDescription = if (allChecked) "Masquer les vérifications" else "Afficher les vérifications"
        )
    }
}
