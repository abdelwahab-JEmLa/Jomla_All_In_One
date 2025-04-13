package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.PanieAchates.APP.Views

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

private const val TAG = "C_MainItem_APP2_ID_2"

@Composable
fun C_MainItem_APP2_ID_2(
    modifier: Modifier = Modifier,
    composeKeyVID: Long,
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
) {
    val relative_1_2_ProduitAcheteOperation = _0_HeadOfRepositorys_Repository_Model
        ._1_2_ProduitAcheteOperation_Repository
        .modelDatasSnapList.find { it.vid == composeKeyVID }

    val init_relative_2_1_ProduitsDataBase =
        _0_HeadOfRepositorys_Repository_Model._2_1_ProduitsDataBase_Repository
            .modelDatasSnapList.find {
                it.vid == (relative_1_2_ProduitAcheteOperation
                    ?.produitAcheterID ?: 0)
            }

    var relative_2_1_ProduitsDataBase by remember { mutableStateOf(init_relative_2_1_ProduitsDataBase) }

    // Calculate total quantity from all color operations for this product
    val totalQuantity = _0_HeadOfRepositorys_Repository_Model
        ._1_1_CouleurAcheteOperation_Repository
        .modelDatasSnapList
        .filter {
            it.parentProduitAchateOperationVID == composeKeyVID &&
                    it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
        }
        .sumOf { it.totaleQuantity }

    // Determine if we should use provisional price or regular price
    val provisionalPrice = relative_1_2_ProduitAcheteOperation?.provisoireMonPrix ?: 0.0
    val defaultPrice = relative_2_1_ProduitsDataBase?.monPrixVent ?: 0.0
    val useProvisionalPrice = provisionalPrice > 0.0

    // State for price editing
    var isEditingPrice by remember { mutableStateOf(false) }
    var priceText by remember { mutableStateOf("") } // Start with empty text
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Format price with Euro symbol and proper formatting using NumberFormat
    val formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE)
    val formattedPrice = if (useProvisionalPrice) {
        formatter.format(provisionalPrice).replace("€", "دج")
    } else {
        formatter.format(defaultPrice).replace("€", "دج")
    }

    Card(
        modifier = modifier
            .padding(vertical = 2.dp, horizontal = 4.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Product info row
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = relative_2_1_ProduitsDataBase?.nom ?: "N/A",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    // Box for both total quantity and price
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        // Row to contain both texts side by side
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Total quantity text
                            Text(
                                text = "$totalQuantity",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Added spacing between texts
                            Spacer(
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            // Show either the editable text field or clickable price text
                            if (isEditingPrice) {
                                OutlinedTextField(
                                    value = priceText,
                                    onValueChange = { newValue ->
                                        // Only allow numeric input with optional decimal point
                                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                            priceText = newValue
                                        }
                                    },
                                    modifier = Modifier
                                        .focusRequester(focusRequester),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            // Update the price when Done is pressed
                                            updatePrice(
                                                priceText,
                                                defaultPrice,
                                                relative_1_2_ProduitAcheteOperation,
                                                _0_HeadOfRepositorys_Repository_Model
                                            )
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                            isEditingPrice = false
                                        }
                                    ),
                                    singleLine = true,
                                    label = {
                                        Text(
                                            if (useProvisionalPrice)
                                                formatter.format(provisionalPrice).replace("€", "دج")
                                            else
                                                formatter.format(defaultPrice).replace("€", "دج")
                                        )
                                    }
                                )

                                // Request focus when the text field is shown
                                LaunchedEffect(Unit) {
                                    try {
                                        focusRequester.requestFocus()
                                        keyboardController?.show()
                                    } catch (e: Exception) {
                                        // Silently handle focus request exceptions
                                    }
                                }
                            } else {
                                // Price text with clickable functionality
                                Text(
                                    text = "× $formattedPrice",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (useProvisionalPrice)
                                        MaterialTheme.colorScheme.tertiary
                                    else
                                        MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.clickable {
                                        priceText = "" // Reset to empty when starting to edit
                                        isEditingPrice = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            val couleursAcheteOperationsVIDs =
                _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository
                    .modelDatasSnapList
                    .filter {
                        it.parentProduitAchateOperationVID == composeKeyVID
                                && it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                    }
                    .map { it.vid }

            if (couleursAcheteOperationsVIDs.isNotEmpty()) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        LazyRow(
                        ) {
                            items(couleursAcheteOperationsVIDs) { couleurVId ->
                                D_ColorDetails_APP2_ID_2(
                                    composeKeyVID = couleurVId,
                                    _0_HeadOfRepositorys_Repository_Model = _0_HeadOfRepositorys_Repository_Model,
                                    relative_2_1_ProduitsDataBase_vid = relative_2_1_ProduitsDataBase?.vid,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper function to update the price
private fun updatePrice(
    priceText: String,
    defaultPrice: Double,
    produitAcheteOperation: Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation?,
    repositoryModel: Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
) {
    val newPrice = priceText.toDoubleOrNull() ?: defaultPrice

    produitAcheteOperation?.let { product ->
        val updatedProduct = product.copy(
            provisoireMonPrix = newPrice
        )
        repositoryModel
            ._1_2_ProduitAcheteOperation_Repository
            .updateUnSeulData(updatedProduct)
    }
}
