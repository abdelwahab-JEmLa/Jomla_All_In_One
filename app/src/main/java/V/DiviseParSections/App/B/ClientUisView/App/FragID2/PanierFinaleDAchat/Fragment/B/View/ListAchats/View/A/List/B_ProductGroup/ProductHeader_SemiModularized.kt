package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.B_ProductGroup

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.Dialog_Choisire_Quantity_Modularized
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProductHeader_SemiModularized(
    relative_M1Produit: ArticlesBasesStatsTable,
    viewModel: ZViewModel_Sec1Frag3,
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>,
) {
    val repositorysMainGetter = viewModel.aCentralFacade.repositorysMainGetter

    val listFiltered_M10OperationVentCouleurs_By_M1Produit by derivedStateOf {
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .get_ListFiltered_M10OperationVentCouleurs_By_M1Produit(
            relative_M1Produit
        )
    }

    val allNonTrouve =
        listFiltered_M10OperationVentCouleurs_By_M1Produit.isNotEmpty() && listFiltered_M10OperationVentCouleurs_By_M1Produit.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }


    val onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent

    val hasNonTrouve =
        listFiltered_M10OperationVentCouleurs_By_M1Produit.any { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }

    var shouldShowDialog_quantite_Boit_Par_Carton by remember { mutableStateOf(false) }
    var shouldShowDialog_quantite_Unite_Par_Boit by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (allNonTrouve) {
                        listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    } else {
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                )
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .getSemanticsTag(
                    nomVal = "onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent",
                    data = onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent
                )
                .getSemanticsTag(
                    nomVal = "ventOperationsForProduct",
                    data = listFiltered_M10OperationVentCouleurs_By_M1Produit
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = relative_M1Produit.nom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (allNonTrouve) {
                    Text(
                        text = "Non disponible",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Card_Produit_Nombre_Unites(
                allNonTrouve, relative_M1Produit
            )
            {
                shouldShowDialog_quantite_Unite_Par_Boit = true
            }

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (allNonTrouve) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            val toggled_setIN_Vent_Its_Quantity_Represent =
                                relative_M1Produit.setIN_Vent_Its_Quantity_Represent.toggle()

                            relative_M1Produit.apply {
                                setIN_Vent_Its_Quantity_Represent =
                                    toggled_setIN_Vent_Its_Quantity_Represent
                            }.also {
                                repositorysMainGetter.repo1ProduitInfos.update(
                                    it
                                )
                            }
                        }, modifier = Modifier.size(36.dp)
                    ) {
                        val carton =
                            relative_M1Produit.setIN_Vent_Its_Quantity_Represent == M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton

                        Icon(
                            imageVector = if (carton) Icons.Default.Inventory2
                            else Icons.Default.ViewModule, contentDescription = null, tint = when {
                                allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                carton -> MaterialTheme.colorScheme.primary

                                else -> MaterialTheme.colorScheme.secondary
                            }, modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            shouldShowDialog_quantite_Boit_Par_Carton = true
                        }, modifier = Modifier.size(36.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Numbers,
                                contentDescription = "Quantity per carton",
                                tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.6f
                                )
                                else MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${relative_M1Produit.quantite_Boit_Par_Carton}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.6f
                                )
                                else MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    ToggleButton_SemiModularized_F_Panie(
                        relative_List_M10OperationVentCouleur =relative_List_M10OperationVentCouleur,
                        allNonTrouve = allNonTrouve,
                        hasNonTrouve = hasNonTrouve,
                        viewModel = viewModel,
                        relative_M1Produit = relative_M1Produit
                    )
                }
            }
        }

        if (shouldShowDialog_quantite_Unite_Par_Boit) {
            Dialog_Choisire_Quantity_Modularized(
                old_quantity = relative_M1Produit.nombreUniteInt,
                label = "nombreUniteInt",
            ) { new_Qyt ->
                if (new_Qyt != null) {
                    relative_M1Produit.apply {
                        nombreUniteInt = new_Qyt

                    }.also {
                        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(it)
                    }

                    viewModel.aCentralFacade.repositorysMainSetter.delete_ListM10OperationVentCouleur(
                        listFiltered_M10OperationVentCouleurs_By_M1Produit
                    )
                }
                shouldShowDialog_quantite_Unite_Par_Boit = false
            }
        }

        if (shouldShowDialog_quantite_Boit_Par_Carton) {
            Dialog_Choisire_Quantity_Modularized(
                old_quantity = relative_M1Produit.quantite_Boit_Par_Carton,
                label = "quantite_Boit_Par_Carton",
            ) { new_Qyt ->
                if (new_Qyt != null) {
                    relative_M1Produit.apply {
                        quantite_Boit_Par_Carton = new_Qyt
                    }.also {
                        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(it)
                    }
                }

                shouldShowDialog_quantite_Boit_Par_Carton = false
            }
        }
    }
}

@Composable
fun ToggleButton_SemiModularized_F_Panie(
    aCentralFacade: ACentralFacade= koinInject(),
    repo10OperationVentCouleur: Repo10OperationVentCouleur = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur,
    allNonTrouve: Boolean,
    hasNonTrouve: Boolean,
    viewModel: ZViewModel_Sec1Frag3,
    relative_M1Produit: ArticlesBasesStatsTable?,
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>
) {
    IconButton(
        onClick = {
            relative_List_M10OperationVentCouleur.map {vent->
                val newState =
                    if (vent.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve)
                        M10OperationVentCouleur.EtateDelivery.NonTrouve
                    else M10OperationVentCouleur.EtateDelivery.Trouve

                repo10OperationVentCouleur.addOrUpdateData(vent.copy(etateDelivery = newState))

            }
        },
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (hasNonTrouve) MaterialTheme.colorScheme.errorContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
            )
    ) {
        Icon(
            imageVector = if (hasNonTrouve) Icons.Default.Cancel else Icons.Default.CheckCircle,
            contentDescription = if (hasNonTrouve) "Mark as found" else "Mark as not found",
            tint = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
            else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f),
            modifier = Modifier.size(20.dp)
        )
    }
}
@Composable
private fun Card_Produit_Nombre_Unites(
    allNonTrouve: Boolean,
    relative_Produit: ArticlesBasesStatsTable,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    onClick_PourOuvrireDialog: () -> Unit
) {
    var toggleState by remember { mutableStateOf(relative_Produit.afficheUniteAuPrint) }

    fun clickHandel() {
        toggleState = !toggleState

        repositorysMainSetter.update_M1Produit(
            relative_Produit.copy(
                afficheUniteAuPrint = toggleState
            )
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allNonTrouve) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(start = petitePaddine)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(petitePaddine),
            modifier = Modifier.padding(petitePaddine)
        ) {
            // Original quantity display button
            IconButton(
                onClick = {
                    onClick_PourOuvrireDialog()
                },
                modifier = Modifier
                    .width(50.dp)
                    .height(36.dp)
            ) {
                Row {
                    Text(
                        text = "Nbr.U ",
                        fontSize = 8.sp,
                        color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f
                        )
                        else MaterialTheme.colorScheme.tertiary,
                    )
                    Text(
                        text = "${relative_Produit.nombreUniteInt}",
                        fontSize = 15.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Toggle button for afficheUniteAuPrint
            IconButton(
                onClick = { clickHandel() },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (toggleState) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    imageVector = if (toggleState) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = if (toggleState) "Print units enabled" else "Print units disabled",
                    tint = if (toggleState) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
