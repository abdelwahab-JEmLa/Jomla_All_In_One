package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.ViewProduit.View.List.C.ViewVentCouleur_T1.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ImageDisplayerGlide_Sec2FragID2
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.ViewProduit.View.List.C.ViewVentCouleur_T1.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.ModernQuantityDialog_T1
import V.DiviseParSections.App.Shared.Repository.B1CouleurOuGoutProduitDataBase
import V.DiviseParSections.App.Shared.Repository.B1CouleurOuGoutProduitDataBaseRepository
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

@SuppressLint("UnrememberedMutableState")
@Composable
fun ViewVentCouleur_T1(
    modifier: Modifier = Modifier,
    ventKey: String,
    b1CouleurOuGoutProduitDataBaseRepository: B1CouleurOuGoutProduitDataBaseRepository = koinInject(),
    size: Dp = 200.dp,
    purchasedQuantity: Int = 0,
    viewModel: ViewModelsProduit_T1
) {
    val vent = viewModel.uiStateCentralRepositorys.fVentCouleurOperationRepository
        .datasValue.find { it.keyID == ventKey }
    val data = vent?.let { v ->
        b1CouleurOuGoutProduitDataBaseRepository.datasValue
            .find { it.key == v.parentCouleurInfosKeyID }
    }

    if (data == null) {
        Card(modifier = modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
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

    // Use ViewModel state instead of local state
    val dialogStates by viewModel.dialogStates.collectAsState()
    val showQuantityDialog = dialogStates.quantityDialogStates[ventKey] ?: false

    val haptic = LocalHapticFeedback.current

    // Use ViewModel business logic functions
    val isRemoved = viewModel.isVentRemoved(vent)
    val itemAlpha = viewModel.getItemAlpha(isRemoved)
    val colorMatrix = if (isRemoved) ColorMatrix().apply { setToSaturation(0f) } else null

    val imageFile by derivedStateOf {
        viewModel.getImageFile(data.nomImageFichieSansEtansion, data.extensionDisponible)
    }

    Card(modifier = modifier.fillMaxWidth().alpha(itemAlpha)) {
        Column(modifier = Modifier.fillMaxSize().padding(5.dp)) {
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
