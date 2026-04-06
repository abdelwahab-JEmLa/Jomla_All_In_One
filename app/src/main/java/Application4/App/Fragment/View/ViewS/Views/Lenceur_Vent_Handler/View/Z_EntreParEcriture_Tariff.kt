package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EntreParEcriture_Tariff(
    modifier: Modifier = Modifier,
    relative_M1produit: M01Produit,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    onTariffSelected: (M13TarificationInfos) -> Unit = {},
    compactMode: Boolean = false,
    isSelected: Boolean = false,
    listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state: List<M10OperationVentCouleur>? = uiState_NewProtoPatterns_viewModel.second
        .active_Datas
        .listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state,
) {
    val viewModel = uiState_NewProtoPatterns_viewModel.second
    var textValue by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val hPad = if (compactMode) TariffTextSizes.COMPACT_HORIZONTAL_PADDING else TariffTextSizes.NORMAL_HORIZONTAL_PADDING
    val vPad = if (compactMode) TariffTextSizes.COMPACT_VERTICAL_PADDING else TariffTextSizes.NORMAL_VERTICAL_PADDING
    val fontSize = if (compactMode) TariffTextSizes.COMPACT_MAIN_TEXT else TariffTextSizes.NORMAL_MAIN_TEXT
    val secondaryFontSize = if (compactMode) TariffTextSizes.COMPACT_SECONDARY_TEXT else TariffTextSizes.NORMAL_SECONDARY_TEXT

    val bg = Color(0xFFFFEB3B).copy(alpha = if (isSelected) 1f else 0.9f)
    val borderWidth = if (isSelected) TariffTextSizes.SELECTED_BORDER_WIDTH else TariffTextSizes.UNSELECTED_BORDER_WIDTH
    val borderColor = if (isSelected) Color.Red else Color.Transparent
    val textStyle = TextStyle(
        color = Color.Black,
        fontSize = fontSize,
        textAlign = TextAlign.Center
    )

    val newTariff by remember {
        derivedStateOf {
            val price = textValue.toDoubleOrNull() ?: 0.0
            if (price > 0.0) M13TarificationInfos.get_default().copy(
                typeChoisi = M13TarificationInfos.TypeChoisi.Edited_Pour_Client,
                prixCurrency = price,
                parent_M1Produit_KeyId = relative_M1produit.keyID,
                parent_M1Produit_DebugInfos = relative_M1produit.getDebugInfos(),
            ) else null
        }
    }

    val existingEditedPrix by remember(
        listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state,
        relative_M1produit.keyID
    ) {
        derivedStateOf {
            listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
                ?.find {
                    it.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                            it.typeTarificationEnumT2 == M13TarificationInfos.TypeChoisi.Edited_Pour_Client
                }
                ?.prix_de_Vent_entre_directement_NewProto
                ?.takeIf { it > 0.0 }
        }
    }

    val displayPrice by remember(textValue, existingEditedPrix) {
        derivedStateOf { textValue.toDoubleOrNull()?.takeIf { it > 0.0 } ?: existingEditedPrix }
    }
    val nombreUnite = relative_M1produit.nombreUniteInt
    val clientPrixVentUnite = relative_M1produit.clientPrixVentUnite
    val prixUnitaire by remember(displayPrice) {
        derivedStateOf { displayPrice?.let { if (nombreUnite > 1) it / nombreUnite else null } }
    }
    val beneficeClient by remember(displayPrice) {
        derivedStateOf {
            displayPrice?.let { prix ->
                if (clientPrixVentUnite > 0) clientPrixVentUnite * nombreUnite - prix else null
            }
        }
    }

    val showInfoOnTop = isFocused && displayPrice != null

    Column(
        modifier = modifier
            .border(width = borderWidth, color = borderColor, shape = CircleShape)
            .background(bg, CircleShape)
            .padding(horizontal = hPad, vertical = vPad),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        if (showInfoOnTop) {
            val benef = beneficeClient
            if (benef != null) {
                Text(
                    text = "bén: ${formatPrice(benef)} DA",
                    color = if (benef >= 0) Color.Black.copy(alpha = 0.75f) else Color.Red.copy(alpha = 0.85f),
                    fontSize = secondaryFontSize,
                    lineHeight = secondaryFontSize,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // FIX TODO(1): centered decoration box + reduced width
        BasicTextField(
            value = textValue,
            onValueChange = { raw ->
                val f = raw.filter { it.isDigit() || it == '.' }
                if (f.count { it == '.' } <= 1) {
                    textValue = f
                    val livePrice = f.toDoubleOrNull() ?: 0.0
                    if (livePrice > 0.0) {
                        onTariffSelected(
                            M13TarificationInfos.get_default().copy(
                                typeChoisi = M13TarificationInfos.TypeChoisi.Edited_Pour_Client,
                                prixCurrency = livePrice,
                                parent_M1Produit_KeyId = relative_M1produit.keyID,
                            )
                        )
                    }
                }
            },
            textStyle = textStyle,
            cursorBrush = SolidColor(Color.Black),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                val t = newTariff
                if (t != null) {
                    val now = System.currentTimeMillis()
                    val finalTariff = t.copy(
                        creationTimestamps = now,
                        dernierTimeTampsSynchronisationAvecFireBase = now,
                    )
                    viewModel.update_M13TarificationInfos(finalTariff)
                    val activeList = listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
                    val productOps = activeList?.filter { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
                    if (!productOps.isNullOrEmpty()) {
                        viewModel.update_listM10OperationVentCouleur(activeList.map { op ->
                            if (op.parent_M1Produit_KeyId == relative_M1produit.keyID)
                                op.copy(
                                    parentM13TarificationKeyID = finalTariff.keyID,
                                    parentM13TarificationDebugInfos = finalTariff.getDebugInfos(),
                                    prix_de_Vent_entre_directement_NewProto = finalTariff.prixCurrency,
                                    typeTarificationEnumT2 = finalTariff.typeChoisi,
                                    dernierTimeTampsSynchronisationAvecFireBase = now,
                                )
                            else op
                        })
                    }
                    onTariffSelected(finalTariff)
                    textValue = ""
                }
                isFocused = false
                keyboardController?.hide()
                focusManager.clearFocus()
            }),
            decorationBox = { innerTextField ->
                // FIX TODO(1): Box ensures placeholder and cursor are both centered
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (textValue.isEmpty()) {
                        val placeholderText = existingEditedPrix?.let { formatPrice(it) } ?: "Prix…"
                        val placeholderAlpha = if (existingEditedPrix != null) 0.75f else 0.45f
                        Text(
                            text = placeholderText,
                            color = Color.Black.copy(alpha = placeholderAlpha),
                            fontSize = fontSize,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                }
            },
            // FIX TODO(1): reduced max width so the field stays compact
            modifier = Modifier
                .widthIn(min = 40.dp, max = 64.dp)
                .onFocusChanged { isFocused = it.isFocused },
        )

        val pu = prixUnitaire
        val benef = beneficeClient
        if (!showInfoOnTop) {
            if (pu != null) {
                Text(
                    text = "(${formatPriceWithDecimals(pu)}/u)",
                    color = Color.Black.copy(alpha = 0.8f),
                    fontSize = secondaryFontSize,
                    lineHeight = secondaryFontSize,
                    textAlign = TextAlign.Center,
                )
            }
            if (benef != null) {
                Text(
                    text = "bén: ${formatPrice(benef)} DA",
                    color = if (benef >= 0) Color.Black.copy(alpha = 0.75f) else Color.Red.copy(alpha = 0.85f),
                    fontSize = secondaryFontSize,
                    lineHeight = secondaryFontSize,
                    textAlign = TextAlign.Center,
                )
            }
        } else if (pu != null) {
            Text(
                text = "(${formatPriceWithDecimals(pu)}/u)",
                color = Color.Black.copy(alpha = 0.8f),
                fontSize = secondaryFontSize,
                lineHeight = secondaryFontSize,
                textAlign = TextAlign.Center,
            )
        }
    }
}
