package Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel.ViewModel_A4FragID1
import Z_CodePartageEntreApps.View.GlideDisplayImageByNewProduitModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Composable
fun C_MainItemF1(
    mainItem: A_Produit,
    modifier: Modifier = Modifier,
    onClickOnMain: () -> Unit = {},
    position: Int? = null,
    viewModel: ViewModel_A4FragID1,
) {
    val imageHeight = 90.dp
    val cardWidth = 120.dp

    // Create local state that will be updated and trigger recomposition
    var localProbablementNonDispo by remember { mutableStateOf(mainItem.probablementNonDispo) }
    var localEnumVarNonDispoPourClients by remember { mutableStateOf(mainItem.enumVarNonDispoPourClients) }

    Column(
        modifier = modifier.width(cardWidth),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card for the image
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (position != null)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clickable { onClickOnMain() }
        ) {
            GlideDisplayImageByNewProduitModel(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight),
                imageGlidReloadTigger = mainItem.imageGlidReloadTigger,
                mainItem = mainItem,
                size = imageHeight,
            )
        }

        // ID Text Display
        Text(
            text = "ID: ${mainItem.id}",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )

        // Warning icon button in its own card with red/green background
        Card(
            shape = RoundedCornerShape(10),
            colors = CardDefaults.cardColors(
                containerColor = if (localProbablementNonDispo)
                    MaterialTheme.colorScheme.error // Red background when warning
                else
                    Color(0xFF4CAF50), // Green background when done
                contentColor = Color.White // White content
            ),
            modifier = Modifier
                .fillMaxWidth() // Fill max parent width
                .padding(top = 4.dp)
        ) {
            IconButton(
                onClick = {
                    viewModel.viewModelScope.launch {
                        // Update local state first to trigger immediate UI upsertLenceCommandeRepoGroupedProtoAvantJuin3
                        localProbablementNonDispo = !localProbablementNonDispo

                        // Then upsertLenceCommandeRepoGroupedProtoAvantJuin3 the model
                        mainItem.probablementNonDispo = localProbablementNonDispo

                        // Update the item in the repository and notify
                        viewModel.ProduitUpdateUnSeul(mainItem)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth() // Make the button fill the card width
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (localProbablementNonDispo)
                        Icons.Filled.Warning
                    else
                        Icons.Filled.Done,
                    contentDescription = "Toggle availability",
                    tint = Color.White, // Always white for contrast
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(10),
            colors = CardDefaults.cardColors(
                containerColor = localEnumVarNonDispoPourClients.color,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            IconButton(
                onClick = {
                    viewModel.viewModelScope.launch {
                        // Update local state for immediate UI upsertLenceCommandeRepoGroupedProtoAvantJuin3
                        localEnumVarNonDispoPourClients = when (localEnumVarNonDispoPourClients) {
                            A_Produit.NON_DISPO_POUR_CLIENTS.TOUT ->
                                A_Produit.NON_DISPO_POUR_CLIENTS.NEVEAU

                            A_Produit.NON_DISPO_POUR_CLIENTS.NEVEAU ->
                                A_Produit.NON_DISPO_POUR_CLIENTS.DEFINIE

                            A_Produit.NON_DISPO_POUR_CLIENTS.DEFINIE ->
                                A_Produit.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT

                            A_Produit.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT ->
                                A_Produit.NON_DISPO_POUR_CLIENTS.TOUT
                        }

                        // Update the model
                        mainItem.enumVarNonDispoPourClients = localEnumVarNonDispoPourClients

                        // Update repository directly
                        viewModel.ProduitUpdateUnSeul(mainItem)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                // In the Text component within the second Card
                Text(
                    text = localEnumVarNonDispoPourClients.name,
                    color = Color.White,
                    style = if (localEnumVarNonDispoPourClients !=
                        A_Produit.NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT
                    ) {
                        MaterialTheme.typography.bodyMedium
                    } else {
                        MaterialTheme.typography.bodySmall
                    }
                )
            }
        }
    }
}
