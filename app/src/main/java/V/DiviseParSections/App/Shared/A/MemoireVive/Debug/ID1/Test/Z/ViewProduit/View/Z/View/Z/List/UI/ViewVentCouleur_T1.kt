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
    modifier: Modifier = Modifier,
    ventKey: String,
    size: Dp = 200.dp,
    purchasedQuantity: Int = 0,
    viewModel: ViewModelsProduit_T1,
    produit: ArticlesBasesStatsTable?
) {
    val b1CouleurOuGoutProduitDataBaseRepository =
        viewModel.b1CouleurOuGoutProduitDataBaseRepository
    val fVentCouleurOperationRepository = viewModel.fVentCouleurOperationRepository
    val zAppComptRepositoryComposable = viewModel.getter.zAppComptRepositoryComposable
    val getter = viewModel.aCentral.getter
    val onVentData = getter.gBonVentRepository.onVentData

    val vent = fVentCouleurOperationRepository.datasValue.find { it.keyID == ventKey }

    // CORRECTION 1: Chercher la couleur par son keyID directement
    val data = vent?.let { v ->
        b1CouleurOuGoutProduitDataBaseRepository.datasValue.find {
            it.key == v.parentCouleurInfosKeyID
        }
    } ?: run {
        // CORRECTION 2: Si pas de vent, essayer de trouver la couleur par le produit
        produit?.let { p ->
            b1CouleurOuGoutProduitDataBaseRepository.datasValue.find {
                it.parentBProduitOldID == p.id
            }
        }
    }

    val dialogStates by viewModel.dialogStates.collectAsState()
    val showQuantityDialog = dialogStates.quantityDialogStates[ventKey] ?: false

    val isRemoved = viewModel.isVentRemoved(vent)
    val itemAlpha = viewModel.getItemAlpha(isRemoved)
    val colorMatrix = if (isRemoved) ColorMatrix().apply { setToSaturation(0f) } else null

    val imageFile by derivedStateOf {
        data?.let {
            viewModel.getImageFile(
                it.nomImageFichieSansEtansion,
                data.extensionDisponible
            )
        }
    }

    val haptic = LocalHapticFeedback.current
    val productKeyId = produit?.keyID ?: ""

    val relatedVents by remember {
        derivedStateOf {
            fVentCouleurOperationRepository.datasValue
                .filter { it.parentBProduitInfosKeyId == productKeyId }
                .ifEmpty {
                    val currentAppCompt = zAppComptRepositoryComposable.currentAppCompt
                    listOf(
                        FCouleurVentOperationInfos(
                            parentZAppComptID = currentAppCompt?.keyID ?: "Non Definie",
                            parentDebugInfosID9AppCompt = currentAppCompt?.nom ?: "Non Definie",
                            parentHVentPeriodKeyId = ParametresAppComptNonSaved().activePeriodKeyId,
                            parentDebugInfosID7VentPeriod = ParametresAppComptNonSaved().parentDebugInfosID7VentPeriod,
                            parentGBonVentKeyId = onVentData.keyID,
                            parentDebugInfosID8BonVent = onVentData.nomClientConcerned,
                            parentBProduitInfosKeyId = productKeyId,
                            parentDebugInfosID1Produit = produit?.nom ?: "Non Definie",
                            // CORRECTION 3: Ajouter la relation avec la couleur
                            parentCouleurInfosKeyID = data?.key ?: ""
                        )
                    )
                }
        }
    }

    val modifierAvecSemanticsTestTag = Modifier.semantics(mergeDescendants = true) {
        set(
            SemanticsPropertyKey("1 relativeVent"),
            relatedVents.first()
        )
        set(
            SemanticsPropertyKey("4 onVentData"),
            onVentData
        )
    }

    // CORRECTION 4: Améliorer la gestion du cas où data est null
    if (data == null) {
        // Au lieu d'afficher une erreur, créer une couleur par défaut
        Card(
            modifier = modifierAvecSemanticsTestTag
                .fillMaxWidth()
                .alpha(itemAlpha)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(size),
                    contentAlignment = Alignment.Center
                ) {
                    // Afficher un placeholder avec le nom du produit
                    ColorNameDisplayer_Sec2FragID2(
                        modifier = Modifier.size(size),
                        colorName = produit?.nom ?: "Couleur inconnue",
                        onClickToOpenWindow = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.showQuantityDialog(ventKey)
                        }
                    )

                    if (purchasedQuantity > 0 && !isRemoved) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(
                                        text = purchasedQuantity.toString(),
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
        return
    }

    Card(
        modifier = modifierAvecSemanticsTestTag
            .fillMaxWidth()
            .alpha(itemAlpha)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                when (data.aAffiche) {
                    B1CouleurOuGoutProduitDataBase.Type.Image -> {
                        ImageDisplayerGlide_Sec2FragID2(
                            modifier = Modifier.size(size),
                            imageFile = imageFile,
                            colorName = data.nomCouleurStrSiSonImageDispo,
                            contentScale = ContentScale.Crop,
                            imageSize = DpSize(size, size),
                            colorFilter = colorMatrix?.let { ColorFilter.colorMatrix(it) },
                            onClickToOpenWindow = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.showQuantityDialog(ventKey)
                            }
                        )
                    }

                    B1CouleurOuGoutProduitDataBase.Type.Nom -> {
                        ColorNameDisplayer_Sec2FragID2(
                            modifier = Modifier.size(size),
                            colorName = data.nomCouleurStrSiSonImageDispo,
                            onClickToOpenWindow = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.showQuantityDialog(ventKey)
                            }
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

                if (purchasedQuantity > 0 && !isRemoved) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(
                                    text = purchasedQuantity.toString(),
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

    if (showQuantityDialog) {
        vent?.let {
            ModernQuantityDialog_T1(
                colorName = data.nomCouleurStrSiSonImageDispo,
                currentQuantity = purchasedQuantity,
                onDissmiss_showQuantityDialog = { viewModel.hideQuantityDialog(ventKey) },
                onDismiss = { viewModel.hideQuantityDialog(ventKey) },
                viewModel = viewModel,
                vent = it
            )
        }
    }
}
