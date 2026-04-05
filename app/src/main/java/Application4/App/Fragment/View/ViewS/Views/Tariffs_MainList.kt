package Application4.App.Fragment.View.ViewS.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun Tariffs_MainList(
    modifier: Modifier,
    sortedTariffs: List<M13TarificationInfos>,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    relative_M1produit: M01Produit,
    selectedTariff_Par_AncienProto: M13TarificationInfos,
    compactMode: Boolean,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    tariffsList: List<M13TarificationInfos>,
    tariff_Stocked_Au_OperationVent: M13TarificationInfos? = null,
    relative_M10OperationVentCouleur: M10OperationVentCouleur? = null,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        reverseLayout = true,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item(key = "__entre_par_ecriture__") {
            EntreParEcriture_Tariff(
                relative_M1produit = relative_M1produit,
                uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                onTariffSelected = onTariffSelected,
                compactMode = compactMode,
            )
        }
        items(sortedTariffs, key = { it.keyID }) { tariff ->
            TariffItemSelector(
                uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                tariff = tariff,
                relative_M1produit = relative_M1produit,
                isSelected = tariff.keyID == selectedTariff_Par_AncienProto.keyID,
                compactMode = compactMode,
                onClick = { onTariffSelected(tariff) },
                tariffsList = tariffsList,
            )
        }
    }
}

@Composable
fun EntreParEcriture_Tariff(
    modifier: Modifier = Modifier,
    relative_M1produit: M01Produit,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    onTariffSelected: (M13TarificationInfos) -> Unit = {},
    compactMode: Boolean = false,
) {
    val viewModel = uiState_NewProtoPatterns_viewModel.second
    var textValue by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val hPad = if (compactMode) TariffTextSizes.COMPACT_HORIZONTAL_PADDING else TariffTextSizes.NORMAL_HORIZONTAL_PADDING
    val vPad = if (compactMode) TariffTextSizes.COMPACT_VERTICAL_PADDING else TariffTextSizes.NORMAL_VERTICAL_PADDING
    val fontSize = if (compactMode) TariffTextSizes.COMPACT_MAIN_TEXT else TariffTextSizes.NORMAL_MAIN_TEXT
    val bg = Color(0xFFFFEB3B)
    val textStyle = TextStyle(color = Color.Black, fontSize = fontSize, textAlign = TextAlign.Center)

    // Same pill wrapper as TariffItem / EditableProgressiveTariffItem
    Box(
        modifier = modifier
            .background(bg, CircleShape)
            .padding(horizontal = hPad, vertical = vPad),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = textValue,
            onValueChange = { raw ->
                val f = raw.filter { it.isDigit() || it == '.' }
                if (f.count { it == '.' } <= 1) textValue = f
            },
            textStyle = textStyle,
            cursorBrush = SolidColor(Color.Black),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                val price = textValue.toDoubleOrNull() ?: 0.0
                if (price > 0.0) {
                    val now = System.currentTimeMillis()
                    val newTariff = M13TarificationInfos.get_default().copy(
                        typeChoisi = M13TarificationInfos.TypeChoisi.Edited_Pour_Client,
                        prixCurrency = price,
                        parent_M1Produit_KeyId = relative_M1produit.keyID,
                        creationTimestamps = now,
                        dernierTimeTampsSynchronisationAvecFireBase = now,
                    )
                    viewModel.update_M13TarificationInfos(newTariff)
                    val activeList = viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
                    val productOps = activeList?.filter { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
                    if (!productOps.isNullOrEmpty()) {
                        viewModel.update_listM10OperationVentCouleur(activeList.map { op ->
                            if (op.parent_M1Produit_KeyId == relative_M1produit.keyID)
                                op.copy(
                                    parentM13TarificationKeyID = newTariff.keyID,
                                    parentM13TarificationDebugInfos = newTariff.getDebugInfos(),
                                    prix_de_Vent_entre_directement_NewProto = newTariff.prixCurrency,
                                    typeTarificationEnumT2 = newTariff.typeChoisi,
                                    dernierTimeTampsSynchronisationAvecFireBase = now,
                                )
                            else op
                        })
                    }
                    onTariffSelected(newTariff)
                    textValue = ""
                }
                keyboardController?.hide()
                focusManager.clearFocus()
            }),
            decorationBox = { innerTextField ->
                if (textValue.isEmpty()) {
                    Text("Prix…", color = Color.Black.copy(alpha = 0.45f), fontSize = fontSize, textAlign = TextAlign.Center)
                }
                innerTextField()
            },
            modifier = Modifier.widthIn(min = 48.dp, max = 88.dp),
        )
    }
}
