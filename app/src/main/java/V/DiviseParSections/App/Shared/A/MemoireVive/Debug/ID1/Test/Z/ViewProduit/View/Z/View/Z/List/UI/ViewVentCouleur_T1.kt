package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ImageDisplayerGlide_Sec2FragID2
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.ModernQuantityDialog_T1
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B1CouleurOuGoutProduitDataBase
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun ViewVentCouleur_T1(
    color: B1CouleurOuGoutProduitDataBase,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    viewModel: ViewModelsProduit_T1,
    produit: ArticlesBasesStatsTable?
) {
    val existingVent by remember(produit?.keyID, color.key) {
        derivedStateOf { viewModel.calculateExistingVent(produit, color) }
    }

    val appCompt = viewModel.getter.zAppComptRepositoryComposable.currentAppCompt
    val onVentData = viewModel.aCentral.getter.gBonVentRepository.onVentData
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    val defaultVent by remember {
        derivedStateOf { viewModel.createDefaultVent(color, produit, appCompt, onVentData) }
    }

    val ventUIState = remember(existingVent, uiState) {
        derivedStateOf { viewModel.calculateUIState(existingVent, uiState) }
    }.value

    val imageFile by derivedStateOf {
        viewModel.getImageFile(color.nomImageFichieSansEtansion, color.extensionDisponible)
    }

    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(SemanticsPropertyKey("1 relativeVent"), existingVent ?: defaultVent)
                set(SemanticsPropertyKey("4 onVentData"), onVentData)
            }
            .fillMaxWidth()
            .alpha(ventUIState.itemAlpha)
            .graphicsLayer(alpha = if (existingVent?.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve) 0.5f else 1.0f)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(5.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                when (color.aAffiche) {
                    B1CouleurOuGoutProduitDataBase.Type.Image -> {
                        ImageDisplayerGlide_Sec2FragID2(
                            ventKey = ventUIState.ventKey,
                            modifier = Modifier.size(size),
                            imageFile = imageFile,
                            colorName = color.nomCouleurStrSiSonImageDispo,
                            contentScale = ContentScale.Crop,
                            imageSize = DpSize(size, size),
                            colorFilter = ventUIState.colorMatrix?.let { ColorFilter.colorMatrix(it) },
                            onClickToOpenWindow = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                val vent = existingVent ?: defaultVent
                                if (existingVent == null) {
                                    viewModel.fVentCouleurOperationRepository.addOrUpdateData(vent)
                                }
                                viewModel.showQuantityDialog(vent.keyID)
                            }
                        )
                    }
                    B1CouleurOuGoutProduitDataBase.Type.Nom -> {
                        ColorNameDisplayer_Sec2FragID2(
                            modifier = Modifier.size(size),
                            colorName = color.nomCouleurStrSiSonImageDispo,
                            onClickToOpenWindow = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                val vent = existingVent ?: defaultVent
                                if (existingVent == null) {
                                    viewModel.fVentCouleurOperationRepository.addOrUpdateData(vent)
                                }
                                viewModel.showQuantityDialog(vent.keyID)
                            }
                        )
                    }
                }

                if (ventUIState.isRemoved) {
                    Surface(
                        modifier = Modifier.align(Alignment.Center),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "REMOVED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (ventUIState.quantity > 0 && !ventUIState.isRemoved) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(
                                    text = ventUIState.quantity.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Box(modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    if (ventUIState.showDialog && existingVent != null) {
        ModernQuantityDialog_T1(
            colorName = color.nomCouleurStrSiSonImageDispo,
            currentQuantity = ventUIState.quantity,
            onDissmiss_showQuantityDialog = { viewModel.hideQuantityDialog(ventUIState.ventKey) },
            onDismiss = { viewModel.hideQuantityDialog(ventUIState.ventKey) },
            viewModel = viewModel,
            vent = existingVent!!
        )
    }
}
