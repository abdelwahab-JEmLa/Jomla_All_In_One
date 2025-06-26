package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.A.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.BProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.FCouleurVentOperation
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.A.List.C.MainItem.UI.VentDisplayer_Sec2FragId2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.ZViewModel_Sec1Frag3
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

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
@Composable
fun ProductGroup(
    viewModel: ZViewModel_Sec1Frag3,
    productId: String,
    achats: List<FCouleurVentOperation>,
    modifier: Modifier = Modifier,
    bProduitDataBase_SubClassFunctionality: BProduitDataBaseComposeRepositoryPJ17
) {
    val produit = bProduitDataBase_SubClassFunctionality.datasValue.find { it.id.toString() == productId }

    // Calculate total quantity across all colors
    val totalQuantity = achats.sumOf { it.quantityAchete }

    // Get product name with fallback
    val productName = produit?.nom?.takeIf { it.isNotBlank() }
        ?: produit?.nomMutable?.takeIf { it.isNotBlank() }
        ?: "Product #$productId"

    // Get the first item's price for the price editor
    val firstItemPrice = achats.firstOrNull()?.provisoireMonPrix ?: 0.0

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Enhanced product header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = productName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Show product ID if different from name
                        if (productName != "Product #$productId") {
                            Text(
                                text = "ID: $productId",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Total quantity badge with icon
                    Surface(       //<--
                    //TODO(1): fait que ca soit clickable au click affiche ModernQuantityDialog 
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Total quantity",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = totalQuantity.toString(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price Editor for all items in this product group
            PriceEditorFragID2(
                currentPrice = firstItemPrice,
                label = "Prix unitaire pour tous",
                onPriceUpdate = { newPrice ->
                    // Update all achats in this product group with the new price
                    achats.forEach { vent ->
                        val updatedVent = vent.copy(
                            provisoireMonPrix = newPrice,
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        )
                        viewModel.uiStateCentralRepositorys.fCouleurAchatOperationRepositoryComposable.addOrUpdateData(updatedVent)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                additionalInfo = if (firstItemPrice > 0 && totalQuantity > 0) {
                    {
                        val totalPrice = firstItemPrice * totalQuantity
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "Total du produit: ${String.format("%.2f", totalPrice)} DA",
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

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achats) { vent ->
                    val relatedCouleur =
                        viewModel.uiStateCentralRepositorys.b1CouleurOuGoutProduitDataBaseRepository.datasValue
                            .find { it.key == vent.parentCouleurDataBaseKey }

                    relatedCouleur?.let { couleur ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            tonalElevation = 2.dp,
                            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                        ) {
                            VentDisplayer_Sec2FragId2(
                                modifier = Modifier.padding(4.dp),
                                ventKey = vent.keyID,
                                size = 120.dp,
                                purchasedQuantity = vent.quantityAchete,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
