package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter.Companion.getSemanticsTagFocucedVars
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.koinInject

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun QuantityDisplay_Mo_F_Panie(
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    allNonTrouve: Boolean,
    onQuantityClickToHaptic: () -> Unit ,
    aCentralFacade: ACentralFacade= koinInject()
) {
    val getter = viewModel.getterFocusedVarsHandlerFacade

    val totalQuantity by derivedStateOf {
        viewModel.getterFocusedVarsHandlerFacade
            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { ventOperation ->
                ventOperation.parent_M1Produit_KeyId == produit.keyID
            }   .sumOf { it.quantity }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (allNonTrouve) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(enabled = false) {
                    val get = viewModel.focusedVarsHandlerFacade.focusedValuesGetter

                    viewModel.aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                        m13TarificationInfos_Pour_Produit = get.focused_M13TarificationInfos_Pour_Produit,
                        m10OperationVentCouleurs = get.focused_ListM10OpeVentCouleur_Par_PD_M1Produit,
                        aCentralFacade = aCentralFacade
                    )

                    viewModel.setterFocusedVarsHandlerFacade.active_M1Produit_Pour_Choisire_TotalQuantity(
                        produit
                    )

                    onQuantityClickToHaptic()
                }
                .getSemanticsTag(
                    nomVal = "dialogChoisireQuantityM1ProduitInfosDebugName",
                    data = getter.currentActive_M9AppCompt?.dialogChoisireQuantityM1ProduitInfosDebugName
                )
                .getSemanticsTag_By_datas_A_Affiche_Au_Nom(
                    1,
                    "dialogChoisireQuantityM1ProduitInfosKeyID", getter.currentActive_M9AppCompt?.dialogChoisireQuantityM1ProduitInfosKeyID
                )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Total quantity",
                    tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = totalQuantity.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        val datasValue = viewModel.aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue

        val findTariff = datasValue.find { tariff ->
            tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                    tariff.parent_M1Produit_KeyId == produit.keyID
        }
        val default_Tariff = M13TarificationInfos.get_default_P0(produit, start_Prix_Depuit_Ancient = produit.prixAchat)
        val finale_Tariff = findTariff ?: default_Tariff.first

        Surface(
            modifier = Modifier
                .getSemanticsTag(nomVal = "repo13TarificationInfos", data = datasValue)
                .getSemanticsTag_By_datas_A_Affiche_Au_Nom(2, "finale_Tariff", finale_Tariff)
                .getSemanticsTag_By_datas_A_Affiche_Au_Nom(3, "findTariff", findTariff)
                .clickable(enabled = !allNonTrouve) {
                    val aCentral = viewModel.aCentralFacade
                    val focusedVarsHandlerFacade = aCentral.focusedActiveValuesFacade
                    val set = aCentral.repositorysMainSetter

                    set.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                        m13TarificationInfos_Pour_Produit = finale_Tariff,
                        m10OperationVentCouleurs = getter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit,
                        aCentralFacade = aCentralFacade
                    )

                    focusedVarsHandlerFacade.focusedValuesSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
                        produit
                    )

                    onQuantityClickToHaptic()
                },
            shape = RoundedCornerShape(20.dp),
            color = if (allNonTrouve) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.secondary
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

                Text(
                    text = "$depuit_Qui - ${finale_Tariff.prixCurrency}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSecondary
                )

                Icon(
                    imageVector = tariffIcon,
                    contentDescription = if (findTariff != null) "Defined by Ali" else "From old database",
                    tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
@SuppressLint("UnrememberedMutableState")
@Composable
fun VentProduitQuantityDialog_T1(
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    colorName: String,
    currentQuantity: Int,
    on_Pour_FocuceAfficheClavieSearcherProduit: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var selectedQuantity by remember { mutableStateOf(currentQuantity) }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    // Calculate carton quantity
    val cartonQuantity by derivedStateOf {
        if (produit.quantite_Boit_Par_Carton > 0) {
            selectedQuantity / produit.quantite_Boit_Par_Carton
        } else {
            0
        }
    }

    val remainingUnits by derivedStateOf {
        if (produit.quantite_Boit_Par_Carton > 0) {
            selectedQuantity % produit.quantite_Boit_Par_Carton
        } else {
            selectedQuantity
        }
    }

    fun closeDialogChoisireQuantity() {
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = { closeDialogChoisireQuantity() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        title = {
            Column {
                Text(
                    text = "Select Quantity",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = colorName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Carton Quantity Display Card
                if (produit.quantite_Boit_Par_Carton > 0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Carton Breakdown",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$cartonQuantity",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "carton(s)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    if (remainingUnits > 0) {
                                        Text(
                                            text = "+ $remainingUnits unit(s)",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "Units per carton:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "${produit.quantite_Boit_Par_Carton}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Quantity Grid
                QuantityGridM1Produit_T1(
                    produit = produit,
                    currentQuantity = selectedQuantity,
                    onQuantitySelected = { newQuantity ->
                        selectedQuantity = newQuantity
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        val message = if (newQuantity == 0) {
                            "Removed $colorName from cart"
                        } else {
                            "Updated $colorName quantity to $newQuantity"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                        closeDialogChoisireQuantity()
                    },
                    viewModel = viewModel,
                )
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    closeDialogChoisireQuantity()
                },
                modifier = Modifier.getSemanticsTagFocucedVars(viewModel.getterFocusedVarsHandlerFacade)
            ) {
                Text("Close")
            }
        }
    )
}
@Composable
fun QuantityGridM1Produit_T1(
    produit: ArticlesBasesStatsTable,
    currentQuantity: Int,
    onQuantitySelected: (Int) -> Unit,
    viewModel: ViewModelsProduit_T1,
    on_Pour_FocuceAfficheClavieSearcherProduit: () -> Unit = {},
) {
    var showExtendedRange by remember { mutableStateOf(false) }

    val basicQuantities = remember {
        listOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 25, 30, 40, 50
        )
    }

    val extendedQuantities = remember {
        (0..50).toList()
    }

    val quantities = if (showExtendedRange) extendedQuantities else basicQuantities

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showExtendedRange) "All Numbers (0-50)" else "Quick Select",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = { showExtendedRange = !showExtendedRange },
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = if (showExtendedRange) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showExtendedRange) "Show less" else "Show more",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (showExtendedRange) "Less" else "More",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Quantity Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(if (showExtendedRange) 280.dp else 200.dp)
        ) {
            items(quantities.size) { index ->
                val quantityNumber = quantities[index]
                QuantityButtonM1Produit_T1(
                    produit = produit,
                    modifier = Modifier.fillMaxWidth(),
                    viewModel = viewModel,
                    newQuantity = quantityNumber,
                    isSelected = quantityNumber == currentQuantity,
                    onClick = onQuantitySelected ,
                )
            }
        }
    }
}
