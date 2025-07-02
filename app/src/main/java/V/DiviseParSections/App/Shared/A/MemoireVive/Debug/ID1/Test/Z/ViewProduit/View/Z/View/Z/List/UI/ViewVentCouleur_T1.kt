package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ImageDisplayerGlide_Sec2FragID2
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.ModernQuantityDialog_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B1CouleurOuGoutProduitDataBase
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
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

@SuppressLint("UnrememberedMutableState")
@Composable
fun ViewVentCouleur_T1(
    color: B1CouleurOuGoutProduitDataBase,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    viewModel: ViewModelsProduit_T1,
    produit: ArticlesBasesStatsTable?
) {
    val existingVent = remember(produit?.keyID, color.key) {
        derivedStateOf {
            viewModel.fVentCouleurOperationRepository.datasValue.find {
                it.parentBProduitInfosKeyId == produit?.keyID && it.parentCouleurInfosKeyID == color.key
            }
        }
    }.value

    val appCompt = viewModel.getter.zAppComptRepositoryComposable.currentAppCompt
    val onVentData = viewModel.aCentral.getter.gBonVentRepository.onVentData
    val dialogStates by viewModel.dialogStates.collectAsState()
    val haptic = LocalHapticFeedback.current

    val ventKey = existingVent?.keyID ?: ""
    val quantity = existingVent?.quantityAchete ?: 0
    val showDialog = ventKey.isNotEmpty() && (dialogStates.quantityDialogStates[ventKey] ?: false)
    val isRemoved = existingVent?.let { viewModel.isVentRemoved(it) } ?: false
    val itemAlpha = viewModel.getItemAlpha(isRemoved)
    val colorMatrix = if (isRemoved) ColorMatrix().apply { setToSaturation(0f) } else null

    val imageFile by derivedStateOf {
        viewModel.getImageFile(color.nomImageFichieSansEtansion, color.extensionDisponible)
    }

    val defaultVent by remember {
        derivedStateOf {
            FCouleurVentOperationInfos(
                keyID = "vent_${color.key}_${produit?.keyID}",
                parentZAppComptID = appCompt?.keyID ?: "Non Definie",
                parentDebugInfosID9AppCompt = appCompt?.nom ?: "Non Definie",
                parentHVentPeriodKeyId = ParametresAppComptNonSaved().activePeriodKeyId,
                parentDebugInfosID7VentPeriod = ParametresAppComptNonSaved().parentDebugInfosID7VentPeriod,
                parentGBonVentKeyId = onVentData.keyID,
                parentDebugInfosID8BonVent = onVentData.nomClientConcerned,
                parentBProduitInfosKeyId = produit?.keyID ?: "",
                parentDebugInfosID1Produit = produit?.nom ?: "Non Definie",
                parentCouleurInfosKeyID = color.key,
                parentBProduitNomDebug = produit?.nom ?: "",
                parentProduitInfosOldId = produit?.id ?: 0L,
                parentClientName = appCompt?.nom ?: "",
                quantityAchete = 0,
                etateActuellementEst = FCouleurVentOperationInfos.EtateActuellementEst.CreeSlote
            )
        }
    }

    val onItemClick = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        val vent = existingVent ?: defaultVent
        if (existingVent == null) {
            viewModel.fVentCouleurOperationRepository.addOrUpdateData(vent)
        }
        viewModel.showQuantityDialog(vent.keyID)
    }

    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(SemanticsPropertyKey("1 relativeVent"), existingVent ?: defaultVent)
                set(SemanticsPropertyKey("4 onVentData"), onVentData)
            }
            .fillMaxWidth()
            .alpha(itemAlpha)
            .graphicsLayer(alpha = if (existingVent?.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve) 0.5f else 1.0f)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(5.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                when (color.aAffiche) {
                    B1CouleurOuGoutProduitDataBase.Type.Image -> {
                        ImageDisplayerGlide_Sec2FragID2(
                            ventKey = ventKey,
                            modifier = Modifier.size(size),
                            imageFile = imageFile,
                            colorName = color.nomCouleurStrSiSonImageDispo,
                            contentScale = ContentScale.Crop,
                            imageSize = DpSize(size, size),
                            colorFilter = colorMatrix?.let { ColorFilter.colorMatrix(it) },
                            onClickToOpenWindow = onItemClick
                        )
                    }
                    B1CouleurOuGoutProduitDataBase.Type.Nom -> {
                        ColorNameDisplayer_Sec2FragID2(
                            modifier = Modifier.size(size),
                            colorName = color.nomCouleurStrSiSonImageDispo,
                            onClickToOpenWindow = onItemClick
                        )
                    }
                }

                if (isRemoved) {
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

                if (quantity > 0 && !isRemoved) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(
                                    text = quantity.toString(),
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

    if (showDialog && existingVent != null) {
        ModernQuantityDialog_T1(
            colorName = color.nomCouleurStrSiSonImageDispo,
            currentQuantity = quantity,
            onDissmiss_showQuantityDialog = { viewModel.hideQuantityDialog(ventKey) },
            onDismiss = { viewModel.hideQuantityDialog(ventKey) },
            viewModel = viewModel,
            vent = existingVent
        )
    }
}
