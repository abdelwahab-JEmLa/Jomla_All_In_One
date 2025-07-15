package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.UnitEditor
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PriceAndUnitSection(
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
) {       //<--
//TODO(1): ajout au click affiche tariffs
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // First row: Price editors
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (produit.nombreUniteInt > 0 && produit.clientPrixVentUnite > 0) {
                    PriceEditor(
                        currentPrice = produit.clientPrixVentUnite * produit.nombreUniteInt,
                        label = "تخرج",
                        onPriceUpdate = { newClientPrixUnite -> },
                        textColor = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }

                if (produit.nombreUniteInt > 0) {
                    PriceEditor(
                        currentPrice = produit.clientPrixVentUnite,
                        label = "clientPrixVentUnite",
                        onPriceUpdate = { newClientPrixUnite ->
                            updateProduct(produit.copy(clientPrixVentUnite = newClientPrixUnite))
                        },
                        textColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Unit Editor
                UnitEditor(
                    currentUnits = produit.nombreUniteInt,
                    label = "Unités",
                    onUnitsUpdate = { newUnits ->
                        updateProduct(produit.copy(nombreUniteInt = newUnits))
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quantité Boîte par Carton Editor
                UnitEditor(
                    currentUnits = produit.quantite_Boit_Par_Carton,
                    label = "Qté/Carton",
                    onUnitsUpdate = { newQuantity ->
                        updateProduct(produit.copy(quantite_Boit_Par_Carton = newQuantity))
                    },
                    modifier = Modifier.weight(1f)
                )

                // Quantity Representation Dropdown
                QuantityRepresentationDropdown(
                    currentValue = produit.setIN_Vent_Its_Quantity_Represent,
                    onValueChange = { newValue ->
                        updateProduct(produit.copy(setIN_Vent_Its_Quantity_Represent = newValue))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuantityRepresentationDropdown(
    currentValue: M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent,
    onValueChange: (M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val options = M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.entries.toTypedArray()

    Column(modifier = modifier) {
        Text(
            text = "Représentation",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = when (currentValue) {
                    M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit -> "Par Boîte"
                    M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton -> "Par Carton"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = when (option) {
                                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit -> "Par Boîte"
                                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton -> "Par Carton"
                            }
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
