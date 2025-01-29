package Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.Modules

import Z_MasterOfApps.Kotlin.Model.Extension.groupedProductsParClients
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.update_AllProduits
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Composable
fun ClientEditePositionDialog(
    modifier: Modifier = Modifier,
    viewModelProduits: ViewModelInitApp,
) {
    // Access groupedProducts through the viewModel
    val groupedProducts = viewModelProduits._modelAppsFather.groupedProductsParClients

    if (viewModelProduits
            ._paramatersAppsViewModelModel
            .visibilityClientEditePositionDialog
    ) {
        Dialog(
            onDismissRequest = {
                viewModelProduits
                    ._paramatersAppsViewModelModel
                    .visibilityClientEditePositionDialog = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Edit Client Positions",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    groupedProducts.forEachIndexed { index, (clientInfo, produits) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (index > 0) {
                                FloatingActionButton(
                                    onClick = {
                                        viewModelProduits.viewModelScope.launch {
                                            val previousClientInfo =
                                                groupedProducts[index - 1].first

                                            clientInfo.positionDonClientsList--
                                            previousClientInfo.positionDonClientsList++

                                            val updatedProducts =
                                                viewModelProduits._modelAppsFather.produitsMainDataBase.map { product ->
                                                    product.apply {
                                                        bonsVentDeCetteCota.forEach { bonVent ->
                                                            bonVent.clientInformations?.let { currentClientInfo ->
                                                                when (currentClientInfo.id) {
                                                                    clientInfo.id -> {
                                                                        val tempPosition =
                                                                            currentClientInfo.positionDonClientsList
                                                                        currentClientInfo.positionDonClientsList =
                                                                            previousClientInfo.positionDonClientsList
                                                                        previousClientInfo.positionDonClientsList =
                                                                            tempPosition
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            update_AllProduits(
                                                updatedProducts,
                                                viewModelProduits
                                            )
                                        }
                                    },
                                    modifier = Modifier.size(36.dp),
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExpandLess,
                                        contentDescription = "Move Up"
                                    )
                                }
                            }

                            Text(
                                text = "${clientInfo.nom} (${produits.size})",
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (viewModelProduits
                                                ._paramatersAppsViewModelModel
                                                .phoneClientSelectedAcheteur == clientInfo.id
                                        ) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    )
                                    .padding(4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            FloatingActionButton(
                                onClick = {
                                    viewModelProduits
                                        ._paramatersAppsViewModelModel
                                        .phoneClientSelectedAcheteur = clientInfo.id
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = Color(android.graphics.Color.parseColor(clientInfo.couleur))
                            ) {
                                Text(
                                    text = produits.size.toString(),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
