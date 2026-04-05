package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent

import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent.Couleur_Image.ImageDisplayerGlide_FragFastVent
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent.z.Com.ElevatedCardHeader
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Produit_Vent(
    produitKeyId: String,
    ventList: List<M10OperationVentCouleur>,
    positionIndex: Int,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val produit = remember(produitKeyId) {
        repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
    }

    val categoriesMap =
        aCentralFacade.repositorysMainGetter.repoM16CategorieProduit.datasValue.associateBy { it.id }
    val category = produit?.idParentCategorie?.let { categoryId ->
        categoriesMap[categoryId]
    }

    val hasNonTrouve = remember(ventList) {
        ventList.any { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
    }

    val allNonTrouve = remember(ventList) {
        ventList.isNotEmpty() && ventList.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
    }

    // Check if all items are checked
    val allChecked = remember(ventList) {
        ventList.isNotEmpty() && ventList.all { it.premier_Check_Donne }
    }

    // Check if any item has lence_pour_check (warning state)
    val anyLencePourCheck = remember(ventList) {
        ventList.any { it.lence_pour_check }
    }

    val relative_M10OperationVentCouleur = ventList.first()
    val relative_M3CouleurProduit =
        repositorysMainGetter.find_M3CouleurInfos_By_KeyID(relative_M10OperationVentCouleur.parent_M3CouleurProduit_KeyID)

    val uniqueComments = remember(ventList) {
        ventList.mapNotNull { vent ->
            if (vent.commetaire.isNotEmpty()) {
                val couleur = repositorysMainGetter.find_M3CouleurInfos_By_KeyID(vent.parent_M3CouleurProduit_KeyID)
                Pair(vent.commetaire, couleur?.nomCouleurStrSiSonImageDispo ?: "Couleur inconnue")
            } else null
        }.distinctBy { it.first }
    }

    fun upsert_M10OperationVentCouleur(newState: Boolean): Unit {
        ventList.forEach { vent ->
            repositorysMainSetter.upsert_M10OperationVentCouleur(
                vent.copy(premier_Check_Donne = newState)
            )
        }
    }

    // Determine card colors based on state
    val outerCardColor = when {
        anyLencePourCheck -> Color(0xFFFF9800) // Orange for warning state
        allChecked -> Color(0xFFFFEB3B) // Yellow when checked
        hasNonTrouve -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val innerCardColor = when {
        anyLencePourCheck -> Color(0xFFFFB74D) // Lighter orange for inner card
        allChecked -> Color(0xFFFFD54F) // Lighter yellow for inner card
        hasNonTrouve -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when {
        anyLencePourCheck -> Color.Black
        allChecked -> Color.Black
        else -> MaterialTheme.colorScheme.error
    }

    produit?.let { nonNullProduit ->
        Box(modifier = modifier) {
            Card(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {
                        set(value = ventList, key = SemanticsPropertyKey("ventList"))
                    }
                    .semantics(mergeDescendants = true) {
                        set(value = nonNullProduit, key = SemanticsPropertyKey("nonNullProduit"))
                    }
                    .fillMaxWidth()
                    .clickable {
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = outerCardColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    ElevatedCardHeader(
                        produit = nonNullProduit,
                        hasNonTrouve = hasNonTrouve,
                        allNonTrouve = allNonTrouve,
                        ventList = ventList,
                        positionIndex = positionIndex,
                        aCentralFacade = aCentralFacade
                    ) {
                        upsert_M10OperationVentCouleur(it)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val ventListS = if (ventList.size > 1) "[${ventList.size} C]" else ""

                    Card(
                        modifier = modifier
                            .fillMaxWidth()
                        ,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = innerCardColor
                        )
                    ) {
                        val actualImageFile = M3CouleurProduitInfos.decrementing_file_name_si_non_trouve(
                            relative_M3CouleurProduit?.nomImageFichieSansEtansion ?: "",
                            relative_M3CouleurProduit?.extensionDisponible ?: ""
                        )
                        val incrementing_file_name = M3CouleurProduitInfos.incrementing_file_name(
                            relative_M3CouleurProduit?.nomImageFichieSansEtansion ?: "",
                        )

                        Row(
                            modifier = Modifier
                                .semantics(mergeDescendants = true) {
                                    set(
                                        value = incrementing_file_name,
                                        key = SemanticsPropertyKey("incrementing_file_name")
                                    )
                                }
                                .semantics(mergeDescendants = true) {
                                    set(
                                        value = actualImageFile,
                                        key = SemanticsPropertyKey("actualImageFile")
                                    )
                                }
                                .semantics(mergeDescendants = true) {
                                    set(
                                        value = relative_M3CouleurProduit,
                                        key = SemanticsPropertyKey("relative_M3CouleurProduit")
                                    )
                                }
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Text on the left
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(
                                    text = "${nonNullProduit.nom} $ventListS",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )

                                if (nonNullProduit.nomArab.isNotEmpty()) {
                                    Text(
                                        text = nonNullProduit.nomArab,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (relative_M3CouleurProduit != null ) {
                                Box(
                                    modifier = Modifier
                                        .size(size)
                                ) {
                                    ImageDisplayerGlide_FragFastVent(
                                        modifier = Modifier.size(size),
                                        relative_M10OperationVentCouleur = relative_M10OperationVentCouleur,
                                        relative_M3CouleurProduit = relative_M3CouleurProduit,
                                        colorName = relative_M3CouleurProduit.nomCouleurStrSiSonImageDispo,
                                        contentScale = ContentScale.Crop,
                                        imageSize = DpSize(size, size),
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (nonNullProduit.nomMutable.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    anyLencePourCheck -> Color(0xFFFFCC80) // Light orange variant for nomMutable
                                    allChecked -> Color(0xFFFFE082) // Light yellow variant for nomMutable when checked
                                    else -> MaterialTheme.colorScheme.primaryContainer
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = nonNullProduit.nomMutable,
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    anyLencePourCheck -> Color.Black
                                    allChecked -> Color.Black
                                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                                },
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

                    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst

                    if (currentApp_ItsWorkChezGrossisst) {
                        Display_Tariff(
                            relative_List_M10OperationVentCouleur = ventList,
                            relative_produit = nonNullProduit,
                            allNonTrouve = allNonTrouve,
                            aCentralFacade = aCentralFacade
                        )
                    } else {
                        Display_Tariff_NonGrossistContext(
                            relative_List_M10OperationVentCouleur = ventList,
                            relative_produit = nonNullProduit,
                            allNonTrouve = allNonTrouve,
                            aCentralFacade = aCentralFacade
                        )
                    }
                    if (uniqueComments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            uniqueComments.forEach { (comment, colorName) ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            anyLencePourCheck -> Color(0xFFFFE0B2) // Very light orange for comments
                                            allChecked -> Color(0xFFFFF59D) // Very light yellow for comments when checked
                                            else -> MaterialTheme.colorScheme.secondaryContainer
                                        }
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Text(
                                            text = colorName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = when {
                                                anyLencePourCheck -> Color.Black.copy(alpha = 0.7f)
                                                allChecked -> Color.Black.copy(alpha = 0.7f)
                                                else -> MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                            },
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = comment,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = when {
                                                anyLencePourCheck -> Color.Black
                                                allChecked -> Color.Black
                                                else -> MaterialTheme.colorScheme.onSecondaryContainer
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FAB_MoveProduct(
                    modifier = Modifier
                ) {
                    upsert_M10OperationVentCouleur(it)
                }
            }
        }
    } ?: run {
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
                    val currentPosition = heldProduit.position_store_3jamale

                    repositorysMainSetter.upsert_M1Produit(
                        heldProduit.copy(
                            position_store_3jamale = currentPosition,
                            dernier_timeTamps_position_store_3jamale = System.currentTimeMillis(),
                        )
                    )

                    focusedValuesGetter.update_activeCentralValues(
                        activeCentralValues.copy(
                            held_Produit_Pour_Move_Au_Position_Store = null
                        )
                    )
                    onToggle(true)
                }
            },
            modifier = modifier.size(48.dp),
            containerColor = Color(0xFF9C27B0),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Déplacer vers le haut"
            )
        }
    }
}
