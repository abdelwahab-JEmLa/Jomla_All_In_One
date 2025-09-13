package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.f

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFormatterUtils
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.f.z.Com.ElevatedCardHeader
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Genere_Tariffs_currentApp_ItsWorkChezGrossisst
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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

    fun upsert_M10OperationVentCouleur(newState: Boolean): Unit {
        ventList.forEach { vent ->
            repositorysMainSetter.upsert_M10OperationVentCouleur(
                vent.copy(premier_Check_Donne = newState)
            )
        }
    }
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

                    if (nonNullProduit.nomArab.isNotEmpty()) {
                        Text(
                            text = nonNullProduit.nomArab,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }

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
                        color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(
                            alpha = 0.7f
                        )
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TariffDisplay(
                        produit = nonNullProduit,
                        allNonTrouve = allNonTrouve,
                        aCentralFacade = aCentralFacade,
                        ventList = ventList
                    )

                    // Add some bottom padding to accommodate the floating buttons
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Row of FABs at bottom-end of the card
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Toggle Check FAB
                ToggleButton_PremierCheckDonne(
                    ventList = ventList,
                    onToggle = { newState ->
                        upsert_M10OperationVentCouleur(newState)
                    },
                    modifier = Modifier
                )

                // Move Product FAB - only show when there's a held product
                FAB_MoveProduct(
                    modifier = Modifier
                ) {
                    upsert_M10OperationVentCouleur(it)
                }
            }
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
private fun TariffDisplay(
    produit: ArticlesBasesStatsTable,
    allNonTrouve: Boolean,
    aCentralFacade: ACentralFacade,
    ventList: List<M10OperationVentCouleur>
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter
    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst

    // Get the SuperGros tariff for the current product if working with grossist
    val superGrosTariff = if (currentApp_ItsWorkChezGrossisst) {
        Genere_Tariffs_currentApp_ItsWorkChezGrossisst()
            .find_existing_Tariff_Grossist_SuperGros(aCentralFacade, produit)
    } else null

    val datasValue = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue

    val findTariff = datasValue.find { tariff ->
        tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                tariff.parent_M1Produit_KeyId == produit.keyID
    }

    val default_Tariff = M13TarificationInfos.get_default_P0(
        produit,
        start_Prix_Depuit_Ancient = produit.prixAchat
    )
    val finale_Tariff = findTariff ?: default_Tariff.first

    Card(
        modifier = Modifier
            .clickable(enabled = !allNonTrouve) {
                repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                    m13TarificationInfos_Pour_Produit = finale_Tariff,
                    m10OperationVentCouleurs = ventList
                )
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allNonTrouve)
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.error
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val (depuit_Qui, tariffIcon) = if (findTariff != null) {
                "Définie Par Ali" to Icons.Default.TrendingUp
            } else {
                "Depuis Mon Old BaseDonnée" to Icons.Default.History
            }

            val displayText = if (currentApp_ItsWorkChezGrossisst) {
                superGrosTariff?.let { tariff ->
                    "${tariff.prixCurrency} DA"
                } ?: "غير متوفر"
            } else {
                "اضغط لاظهار السعر"
            }

            Text(
                text = displayText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = if (allNonTrouve)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else
                    MaterialTheme.colorScheme.onSecondary
            )

            Icon(
                imageVector = tariffIcon,
                contentDescription = if (findTariff != null) "Defined by Ali" else "From old database",
                tint = if (allNonTrouve)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else
                    MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun FAB_MoveProduct(
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = koinInject(),
    onToggle: (Boolean) -> Unit,
) {
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val shouldShow = activeCentralValues.held_Produit_Pour_Move_Au_Position_Store != null

    if (shouldShow) {
        FloatingActionButton(
            onClick = {
                activeCentralValues.held_Produit_Pour_Move_Au_Position_Store?.let { heldProduit ->
                    val currentPosition = heldProduit.position_store_3jamale ?: 0
                    val newPosition = if (currentPosition > 0) currentPosition - 1 else 0

                    repositorysMainSetter.upsert_M1Produit(
                        heldProduit.copy(
                            position_store_3jamale = newPosition,
                            dernier_timeTamps_position_store_3jamale = System.currentTimeMillis(),

                            )
                    )

                    // Clear the held product after moving
                    focusedValuesGetter.update_activeCentralValues(
                        activeCentralValues.copy(
                            held_Produit_Pour_Move_Au_Position_Store = null
                        )
                    )
                    onToggle(true)
                }
            },
            modifier = modifier.size(48.dp),
            containerColor = Color(0xFF9C27B0), // Purple color
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Déplacer vers le haut"
            )
        }
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
        modifier = modifier.size(48.dp),
        containerColor = if (allChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (allChecked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Icon(
            imageVector = if (allChecked) Icons.Filled.RadioButtonUnchecked else Icons.Filled.Check,
            contentDescription = if (allChecked) "Masquer les vérifications" else "Afficher les vérifications"
        )
    }
}
