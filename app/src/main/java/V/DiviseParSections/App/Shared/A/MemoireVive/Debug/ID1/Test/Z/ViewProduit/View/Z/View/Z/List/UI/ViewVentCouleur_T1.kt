package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ImageDisplayerGlide_Sec2FragID2
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.ModernQuantityDialog_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.GetterFocusedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.SetterFocusedVars
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.M3CouleurProduitInfos
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@SuppressLint("UnrememberedMutableState")
@Composable
fun ViewVentCouleur_T1(
    modifier: Modifier = Modifier,
    m3CouleurProduitInfos: M3CouleurProduitInfos,
    produit: ArticlesBasesStatsTable?,
    viewModel: ViewModelsProduit_T1,
    size: Dp = 200.dp
) {
    val setter = viewModel.setterFocusedVarsHandlerFacade
    val getter = viewModel.aCentral.focusedVarsHandlerFacade.getter

    val uiState by viewModel.uiState.collectAsState()
    val getterFocusedVarsHandlerFacade = viewModel.getterFocusedVarsHandlerFacade

    val existingVent by remember(produit?.keyID, m3CouleurProduitInfos.key) {
        derivedStateOf { viewModel.calculateExistingVent(produit, m3CouleurProduitInfos) }
    }
    val haptic = LocalHapticFeedback.current

    fun handelUiAction(haptic: HapticFeedback) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val ventUIState = remember(existingVent, uiState) {
        derivedStateOf { viewModel.calculateUIState(existingVent, uiState) }
    }.value

    val imageFile by derivedStateOf {
        viewModel.getImageFile(
            m3CouleurProduitInfos.nomImageFichieSansEtansion,
            m3CouleurProduitInfos.extensionDisponible
        )
    }
    val defaultM3CouleurProduitInfos = with(m3CouleurProduitInfos) {
        produit?.let {
            val parentM1ProduitDebugInfos = produit.nom
            getterFocusedVarsHandlerFacade.defaultM3CouleurProduitInfos?.copy(
                debugInfos = parentM1ProduitDebugInfos,
                //---------------------------------Parent M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
                parentM1ProduitInfosKeyId = it.keyID,
                parentM1ProduitDebugInfos = parentM1ProduitDebugInfos,
                //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
                parentM3CouleurProduitInfosKeyID = key,
                parentM3CouleurProduitDebugInfos = parentM1ProduitDebugInfos + indexCouleurDansAncienProto,
            )
        }
    }

    val onVentM3CouleurProduitInfos = getterFocusedVarsHandlerFacade.onVentM3CouleurProduitInfos

    val shouldShowDialog by remember(onVentM3CouleurProduitInfos, m3CouleurProduitInfos.key) {
        derivedStateOf {
            onVentM3CouleurProduitInfos?.parentM3CouleurProduitInfosKeyID == m3CouleurProduitInfos.key
        }
    }

    Card(
        modifier = Modifier
            .getSemanticsTag(
                "defaultM3CouleurProduitInfos", defaultM3CouleurProduitInfos
            )
            .fillMaxWidth()
            .alpha(ventUIState.itemAlpha)
            .graphicsLayer(alpha = if (existingVent?.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve) 0.5f else 1.0f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            fun vent(
                defaultM3CouleurProduitInfos: M10OperationVentCouleur?,
                setter: SetterFocusedVars,
                getter: GetterFocusedVars,
                onVentM3CouleurProduitInfos: M10OperationVentCouleur?
            ) {
                defaultM3CouleurProduitInfos?.let { opVent ->
                    setter.addNewM10OperationVentCouleur(opVent)
                    setter.updateFocuseM9AppCompt(
                        getter.currentM9AppCompt!!.copy(
                            onVentM3CouleurProduitDebugInfos = opVent.debugInfos,
                            onVentM3CouleurProduitInfosKeyID = opVent.keyID
                        )
                    )
                } ?: run {
                    if (onVentM3CouleurProduitInfos != null) {
                        setter.updateFocuseM9AppCompt(
                            getter.currentM9AppCompt!!.copy(
                                onVentM3CouleurProduitDebugInfos = onVentM3CouleurProduitInfos.debugInfos,
                                onVentM3CouleurProduitInfosKeyID = onVentM3CouleurProduitInfos.keyID
                            )
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                when (m3CouleurProduitInfos.aAffiche) {
                    M3CouleurProduitInfos.Type.Image -> {
                        ImageDisplayerGlide_Sec2FragID2(
                            modifier = Modifier.size(size),
                            imageFile = imageFile,
                            colorName = m3CouleurProduitInfos.nomCouleurStrSiSonImageDispo,
                            contentScale = ContentScale.Crop,
                            imageSize = DpSize(size, size),
                            colorFilter = ventUIState.colorMatrix?.let { ColorFilter.colorMatrix(it) },
                            onClickToOpenWindow = {
                                vent(
                                    defaultM3CouleurProduitInfos,
                                    setter,
                                    getter,
                                    onVentM3CouleurProduitInfos
                                )
                                handelUiAction(haptic)
                            },
                        )
                    }

                    M3CouleurProduitInfos.Type.Nom -> {
                        ColorNameDisplayer_Sec2FragID2(
                            modifier = Modifier.size(size),
                            colorName = m3CouleurProduitInfos.nomCouleurStrSiSonImageDispo,
                            onClickToOpenWindow = {
                                vent(
                                    defaultM3CouleurProduitInfos,
                                    setter,
                                    getter,
                                    onVentM3CouleurProduitInfos
                                )
                                handelUiAction(haptic)
                            })
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
                        }, modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Box(modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    // Fixed: Show dialog based on focused state instead of local UI state
    if (shouldShowDialog && existingVent != null) {
        ModernQuantityDialog_T1(
            colorName = m3CouleurProduitInfos.nomCouleurStrSiSonImageDispo,
            currentQuantity = ventUIState.quantity,
            onDissmiss_showQuantityDialog = {
                // Clear the focused state to hide dialog
                setter.updateFocuseM9AppCompt(
                    getter.currentM9AppCompt!!.copy(
                        onVentM3CouleurProduitInfosKeyID = ""
                    )
                )
            },
            onDismiss = {
                // Clear the focused state to hide dialog
                setter.updateFocuseM9AppCompt(
                    getter.currentM9AppCompt!!.copy(
                        onVentM3CouleurProduitInfosKeyID = ""
                    )
                )
            },
            viewModel = viewModel,
            vent = existingVent!!
        )
    }
}
