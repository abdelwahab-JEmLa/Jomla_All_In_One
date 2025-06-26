package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.A.List.C.MainItem.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBaseRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.FCouleurVentOperation
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.A.List.C.MainItem.UI.Quantity.Ui.A.Screen.ModernQuantityDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.W.Modules.ImageDisplayerGlide_Sec2FragID2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.ZViewModel_Sec1Frag3
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.io.File

@Composable
fun PriceEditorFragID2(
    currentPrice: Double,
    label: String,
    onPriceUpdate: (Double) -> Unit,
    modifier: Modifier = Modifier,
    additionalInfo: (@Composable () -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    shouldHideQuickInfoCards: Boolean = false,
    onNextField: (() -> Unit)? = null // New parameter for navigation to next field
) {
    var isEditing by remember { mutableStateOf(false) }
    var tempText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isEditing) {
        if (isEditing) focusRequester.requestFocus()
    }

    fun parsePrice(text: String): Double? {
        return try {
            text.replace(',', '.').toDoubleOrNull()
        } catch (e: Exception) { null }
    }

    fun savePrice() {
        val newPrice = parsePrice(tempText) ?: currentPrice
        onPriceUpdate(newPrice)
        isEditing = false
        tempText = ""

        if (shouldHideQuickInfoCards && onNextField != null) {
            // Navigate to next field instead of hiding keyboard
            onNextField()
        } else {
            keyboardController?.hide()
        }
    }

    // Fixed condition: Always show the PriceEditorFragID2 (removed currentPrice > 0 check)
    if (currentPrice >= 0 || isEditing) {
        Column(modifier = modifier) {
            if (isEditing) {
                OutlinedTextField(
                    value = tempText,
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { char ->
                            char.isDigit() || char == '.' || char == ','
                        }
                        val dotCount = filtered.count { it == '.' }
                        val commaCount = filtered.count { it == ',' }
                        if (dotCount <= 1 && commaCount <= 1 && (dotCount + commaCount) <= 1) {
                            tempText = filtered
                        }
                    },
                    label = { Text("Ancien: $currentPrice DA") },
                    placeholder = { Text("Nouveau prix") },
                    suffix = { Text("DA") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        savePrice()
                        if (shouldHideQuickInfoCards && onNextField != null) {
                            onNextField()
                        }
                    }),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                if (shouldHideQuickInfoCards) {
                    OutlinedTextField(
                        value = tempText,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { char ->
                                char.isDigit() || char == '.' || char == ','
                            }
                            val dotCount = filtered.count { it == '.' }
                            val commaCount = filtered.count { it == ',' }
                            if (dotCount <= 1 && commaCount <= 1 && (dotCount + commaCount) <= 1) {
                                tempText = filtered
                            }
                        },
                        label = { Text("$label: $currentPrice DA") },
                        placeholder = { Text("Nouveau prix") },
                        suffix = { Text("DA") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            savePrice()
                            if (shouldHideQuickInfoCards && onNextField != null) {
                                onNextField()
                            }
                        }),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    // Normal surface display mode
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                isEditing = true
                                tempText = ""
                            },
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = textColor.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$currentPrice DA",
                                style = MaterialTheme.typography.bodyLarge,
                                color = textColor,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
            additionalInfo?.invoke()
        }
    }
}

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
    val repo = viewModel.uiStateCentralRepositorys.fCouleurAchatOperationRepositoryComposable
    val vent = repo.datasValue.find { it.keyID == ventKey }
    val data = vent?.let { v ->
        b1CouleurOuGoutProduitDataBaseRepository.datasValue.find { it.key == v.parentCouleurDataBaseKey }
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

    var showQuantityDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val isRemoved = vent?.etateActuellementEst == FCouleurVentOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE
    val itemAlpha = if (isRemoved) 0.4f else 1.0f
    val colorMatrix = if (isRemoved) ColorMatrix().apply { setToSaturation(0f) } else null

    val imageFile by derivedStateOf {
        if (data.nomImageFichieSansEtansion != "Non Dispo") {
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne",
                "${data.nomImageFichieSansEtansion}.${data.extensionDisponible}")
        } else null
    }

    val onItemClick = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        showQuantityDialog = true
    }

    Card(modifier = modifier.fillMaxWidth().alpha(itemAlpha)) {
        Column(modifier = Modifier.fillMaxSize().padding(5.dp)) {
            // Main content (image/color name)
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
                            onClickToOpenWindow = onItemClick
                        )
                    }
                    B1CouleurOuGoutProduitDataBase.Type.Nom -> {
                        ColorNameDisplayer_Sec2FragID2(
                            modifier = Modifier.size(size),
                            colorName = data.nomCouleurStrSiSonImageDispo,
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

            vent?.let { ventData ->
                Spacer(modifier = Modifier.height(8.dp))

                PriceEditorFragID2(
                    currentPrice = ventData.provisoireMonPrix,
                    label = "Prix unitaire",
                    onPriceUpdate = { newPrice ->
                        val updatedVent = ventData.copy(
                            provisoireMonPrix = newPrice,
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        )
                        repo.addOrUpdateData(updatedVent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    additionalInfo = if (ventData.provisoireMonPrix > 0 && purchasedQuantity > 0) {
                        {
                            val totalPrice = ventData.provisoireMonPrix * purchasedQuantity
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = "Total: ${String.format("%.2f", totalPrice)} DA",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                                )
                            }
                        }
                    } else null
                )
            }
        }
    }

    if (showQuantityDialog) {
        vent?.let {
            ModernQuantityDialog(
                colorName = data.nomCouleurStrSiSonImageDispo,
                currentQuantity = purchasedQuantity,
                onDissmiss_showQuantityDialog = { showQuantityDialog = false },
                onDismiss = { showQuantityDialog = false },
                viewModel = viewModel,
                vent = it
            )
        }
    }
}
