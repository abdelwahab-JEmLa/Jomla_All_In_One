package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.W.Components

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Modules.Ui.C.UI.Integer_Outlined_Displaye_Modularized
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProductHeader_T1(
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    productName: String,
    allNonTrouve: Boolean,
    onQuantityClickToHaptic: () -> Unit
) {
    val onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent = viewModel.getterFocusedVarsHandlerFacade
        .onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent

    val ventOperationsForProduct by derivedStateOf {
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.get_BY_M1Produit_list_m10OperationVentCouleurs(
            produit
        )
    }

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
                    onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent,
                    "onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent"
                )
                .getSemanticsTag(ventOperationsForProduct, "ventOperationsForProduct")
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = productName,
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

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (allNonTrouve)
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    var quantite_Boit_Par_Carton = produit.quantite_Boit_Par_Carton

                    val repositorysMainGetter = viewModel.aCentralFacade.repositorysMainGetter

                    IconButton(
                        onClick = {
                            val toggled_setIN_Vent_Its_Quantity_Represent =
                                produit.setIN_Vent_Its_Quantity_Represent.toggle()

                            produit.apply {
                                setIN_Vent_Its_Quantity_Represent =
                                    toggled_setIN_Vent_Its_Quantity_Represent
                            }.also {
                                repositorysMainGetter.repoM1ProduitInfos.update(
                                    it
                                )
                            }
                            viewModel.aCentralFacade.repositorysMainSetter.m10_delete(
                                ventOperationsForProduct
                            )
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        val carton = produit.setIN_Vent_Its_Quantity_Represent ==
                                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton

                        Icon(
                            imageVector = if (carton)
                                Icons.Default.Inventory2
                            else
                                Icons.Default.ViewModule,
                            contentDescription = null,
                            tint = when {
                                allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                carton ->
                                    MaterialTheme.colorScheme.primary

                                else -> MaterialTheme.colorScheme.secondary
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Integer_Outlined_Displaye_Modularized(
                        currentUnits =
                            quantite_Boit_Par_Carton,
                        label = "",
                        onUnitsUpdate = { newQuantity ->

                            produit.apply {
                                quantite_Boit_Par_Carton =
                                    newQuantity
                            }.also {
                                repositorysMainGetter.repoM1ProduitInfos.update(
                                    it
                                )
                            }
                        },
                        modifier = Modifier.size(width = 100 .dp, height = 60.dp),
                        showOnlyWhenPositive = false,
                        textColor = if (allNonTrouve)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
