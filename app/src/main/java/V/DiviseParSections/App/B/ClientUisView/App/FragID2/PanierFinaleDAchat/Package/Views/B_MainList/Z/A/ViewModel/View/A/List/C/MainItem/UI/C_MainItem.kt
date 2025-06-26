package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.A.List.C.MainItem.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBaseRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.FCouleurVentOperation
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.A.List.C.MainItem.UI.Quantity.Ui.ModernQuantityDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.W.Modules.ImageDisplayerGlide_Sec2FragID2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.ZViewModel_Sec1Frag3
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.io.File

@SuppressLint("UnrememberedMutableState")
@Composable
fun VentDisplayer_Sec2FragId2(
    modifier: Modifier = Modifier,
    ventKey: String,
    b1CouleurOuGoutProduitDataBaseRepository: B1CouleurOuGoutProduitDataBaseRepository = koinInject(),
    size: Dp = 200.dp,
    purchasedQuantity: Int = 0,
    viewModel: ZViewModel_Sec1Frag3
) {
    val fCouleurAchatOperationRepositoryComposable = viewModel.uiStateCentralRepositorys
        .fCouleurAchatOperationRepositoryComposable
    val vent = fCouleurAchatOperationRepositoryComposable.datasValue.find { it.keyID == ventKey }

    val datas = b1CouleurOuGoutProduitDataBaseRepository.datasValue

    // FIX: Use safe call and provide fallback, and use the correct key for lookup
    val data = vent?.let { ventData ->
        datas.find { it.key == ventData.parentCouleurDataBaseKey }
    }

    // Early return if data is not found
    if (data == null) {
        // Show placeholder or error state
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Color data not found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        return
    }

    var showQuantityDialog by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current

    // Check if item should be grayed out (removed from final cart)
    val isRemovedFromCart =
        vent.etateActuellementEst == FCouleurVentOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE

    // Apply visual effects for removed items
    val itemAlpha = if (isRemovedFromCart) 0.4f else 1.0f
    val colorMatrix = if (isRemovedFromCart) {
        ColorMatrix().apply { setToSaturation(0f) } // Grayscale effect
    } else null

    val imageFile by derivedStateOf {
        if (data.nomImageFichieSansEtansion != "Non Dispo") {
            val fileName = "${data.nomImageFichieSansEtansion}.${data.extensionDisponible}"
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", fileName)
        } else null
    }

    // FIX: Move the click handler to the Card level to ensure it captures all clicks
    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(itemAlpha)
            .clickable(enabled = !isRemovedFromCart) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                showQuantityDialog = true
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            when (data.aAffiche) {
                B1CouleurOuGoutProduitDataBase.Type.Image -> {
                    ImageDisplayerGlide_Sec2FragID2(
                        modifier = Modifier.size(size),
                        imageFile = imageFile,
                        colorName = data.nomCouleurStrSiSonImageDispo,
                        contentScale = ContentScale.Crop,
                        imageSize = DpSize(size, size),
                        colorFilter = colorMatrix?.let { ColorFilter.colorMatrix(it) },
                    )
                }

                B1CouleurOuGoutProduitDataBase.Type.Nom -> ColorNameDisplayer_Sec2FragID2(
                    modifier = Modifier.size(size),
                    colorName = data.nomCouleurStrSiSonImageDispo,
                    // FIX: Remove the click handler from here since we handle it at Card level
                    onClickToOpenWindow = {} // Empty lambda to prevent conflicts
                )
            }

            // Show removed indicator
            if (isRemovedFromCart) {
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

            if (purchasedQuantity > 0 && !isRemovedFromCart) {
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

    // Modern Quantity Selection Dialog
    if (showQuantityDialog && !isRemovedFromCart) {
        ModernQuantityDialog(
            colorName = data.nomCouleurStrSiSonImageDispo,
            currentQuantity = purchasedQuantity,
            viewModel = viewModel,
            onQuantitySelected = { newQuantity ->
                vent?.let { existingVent ->
                    val updatedVent = if (newQuantity == 0) {
                        existingVent.copy(
                            quantityAchete = newQuantity,
                            etateActuellementEst = FCouleurVentOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE
                        )
                    } else {
                        existingVent.copy(
                            quantityAchete = newQuantity,
                            etateActuellementEst = FCouleurVentOperation.EtateActuellementEst.ParentBonVentConfirme
                        )
                    }
                    fCouleurAchatOperationRepositoryComposable.addOrUpdateData(updatedVent)
                }
                showQuantityDialog = false
            },
            onDismiss = { showQuantityDialog = false }
        )
    }
}
