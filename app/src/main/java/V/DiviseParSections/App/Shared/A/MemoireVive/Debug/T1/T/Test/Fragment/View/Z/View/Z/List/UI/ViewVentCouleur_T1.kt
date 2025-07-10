package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ImageDisplayerGlide_Sec2FragID2
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.VentCouleurQuantityDialog_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
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
    m3Couleur: M3CouleurProduitInfos,
    produit: ArticlesBasesStatsTable?,
    viewModel: ViewModelsProduit_T1,
    size: Dp = 200.dp
) {
    val setter = viewModel.setterFocusedVarsHandlerFacade
    val getter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

    val uiState by viewModel.uiState.collectAsState()
    val getterFocusedVarsHandlerFacade = viewModel.getterFocusedVarsHandlerFacade
    val parentM1ProduitDebugInfos = produit?.getDebugInfos() ?: "null"


    val haptic = LocalHapticFeedback.current

    fun handelUiAction(haptic: HapticFeedback) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val imageFile by derivedStateOf {
        viewModel.getImageFile(
            m3Couleur.nomImageFichieSansEtansion,
            m3Couleur.extensionDisponible
        )
    }


    val findVent by remember {
        derivedStateOf {
            getter.onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent
                .find { it.parentM3CouleurProduitInfosKeyID == m3Couleur.key }
        }
    }
    val defaultM10Vent = getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()?.copy(
        //---------------------------------Parent M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
        parentM1ProduitInfosKeyId = produit?.keyID ?: null.toString(),
        parentM1ProduitDebugInfos = parentM1ProduitDebugInfos,
        //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
        parentM3CouleurProduitInfosKeyID = m3Couleur.key,
        parentM3CouleurProduitDebugInfos = parentM1ProduitDebugInfos + m3Couleur.indexCouleurDansAncienProto,
        quantity_Par_Boit = 1
    )

    val ventUIState = remember(findVent, uiState) {
        derivedStateOf {
            viewModel.calculateUIState(
                findVent,
                uiState
            )
        }
    }.value

    val shouldShowDialog by remember(findVent, m3Couleur.key) {
        derivedStateOf {
            val onVentM3 =
                viewModel.getterFocusedVarsHandlerFacade.onVentM10VentOperation;onVentM3?.parentM3CouleurProduitInfosKeyID == m3Couleur.key
        }
    }

    Card(
        modifier = Modifier
            .getSemanticsTag(
                defaultM10Vent, "defaultM3CouleurProduitInfos"
            )
            .fillMaxWidth()
            .alpha(ventUIState.itemAlpha)
            .graphicsLayer(alpha = if (findVent?.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve) 0.5f else 1.0f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            fun lenceVent() {
                findVent?.let { findVent ->
                    viewModel.aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond()
                    setter.active_M3Couleur_pour_ouvrire_son_Dialog_choixQuantity(findVent)
                } ?: run {
                    defaultM10Vent?.let { defaultVent ->
                        setter.ajoute_New_M10OperationVentCouleur(defaultVent)
                        viewModel.aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond()
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                when (m3Couleur.aAffiche) {
                    M3CouleurProduitInfos.Type.Image -> {
                        ImageDisplayerGlide_Sec2FragID2(
                            modifier = Modifier.size(size),
                            imageFile = imageFile,
                            colorName = m3Couleur.nomCouleurStrSiSonImageDispo,
                            contentScale = ContentScale.Crop,
                            imageSize = DpSize(size, size),
                            colorFilter = ventUIState.colorMatrix?.let { ColorFilter.colorMatrix(it) },
                            onClickToOpenWindow = {
                                lenceVent()
                                handelUiAction(haptic)
                            },
                        )
                    }

                    M3CouleurProduitInfos.Type.Nom -> {
                        ColorNameDisplayer_Sec2FragID2(
                            modifier = Modifier.size(size),
                            colorName = m3Couleur.nomCouleurStrSiSonImageDispo,
                            onClickToOpenWindow = {
                                lenceVent()

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
                                    text = findVent?.quantity_Par_Boit.toString(),
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

    if (shouldShowDialog && findVent != null) {
        VentCouleurQuantityDialog_T1(
            vent = findVent!!,
            viewModel = viewModel,
            colorName = m3Couleur.nomCouleurStrSiSonImageDispo,
            currentQuantity = ventUIState.quantity,
        )
    }
}
