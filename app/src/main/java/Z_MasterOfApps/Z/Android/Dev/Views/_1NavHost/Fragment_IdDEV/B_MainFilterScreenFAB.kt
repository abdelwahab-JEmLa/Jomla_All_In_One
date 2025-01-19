package Z_MasterOfApps.Z.Android.Dev.Views._1NavHost.Fragment_IdDEV

import Z_MasterOfApps.Kotlin.Model.Extension.grossistsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.update_AllProduits
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MainScreenFilterFAB_F5(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showButtons by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { showButtons = !showButtons },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (showButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = showButtons) {
                Column(horizontalAlignment = Alignment.End) {
                    // Afficher tous les grossists disponibles
                    viewModelInitApp._modelAppsFather.grossistsDisponible.forEachIndexed { index, grossist ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (index > 0) {
                                FloatingActionButton(
                                    onClick = {
                                        viewModelInitApp.viewModelScope.launch {
                                            val previousGrossist = viewModelInitApp._modelAppsFather.grossistsDisponible[index - 1]
                                            grossist.positionInGrossistsList--
                                            previousGrossist.positionInGrossistsList++

                                            update_AllProduits(
                                                viewModelInitApp.produitsMainDataBase.map { product ->
                                                    product.apply {
                                                        bonCommendDeCetteCota?.grossistInformations?.let { currentGrossist ->
                                                            when (currentGrossist.id) {
                                                                grossist.id -> currentGrossist.positionInGrossistsList--
                                                                previousGrossist.id -> currentGrossist.positionInGrossistsList++
                                                            }
                                                        }
                                                    }
                                                },
                                                viewModelInitApp
                                            )
                                        }
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.ExpandLess, null)
                                }
                            }

                            // Afficher le nom du grossist
                            Text(
                                grossist.nom,
                                modifier = Modifier
                                    .background(
                                        if (viewModelInitApp._paramatersAppsViewModelModel
                                                .telephoneClientParamaters.selectedGrossistForServeur == grossist.id
                                        ) Color(0xFF2196F3) else Color.Transparent
                                    )
                                    .padding(4.dp)
                            )

                            FloatingActionButton(
                                onClick = {
                                    viewModelInitApp.viewModelScope.launch {
                                        // Trouver le premier produit "Non Defini"
                                        val nonDefiniProduct = viewModelInitApp.produitsMainDataBase
                                            .firstOrNull { product ->
                                                product.bonCommendDeCetteCota?.grossistInformations?.nom == "Non Defini"
                                            }

                                        // Si un produit "Non Defini" est trouvé, le déplacer vers le grossist sélectionné
                                        nonDefiniProduct?.let { product ->
                                            product.bonCommendDeCetteCota?.let { bonCommande ->
                                                bonCommande.grossistInformations = grossist
                                                updateProduit(
                                                    product = product,
                                                    viewModelProduits = viewModelInitApp
                                                )
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = try {
                                    Color(android.graphics.Color.parseColor(
                                        if (grossist.couleur.startsWith("#")) grossist.couleur
                                        else "#${grossist.couleur}"
                                    ))
                                } catch (e: Exception) {
                                    Color(0xFFFF0000)
                                }
                            ) {
                                // Compter les produits pour ce grossist
                                val productCount = viewModelInitApp.produitsMainDataBase.count { product ->
                                    product.bonCommendDeCetteCota?.grossistInformations?.id == grossist.id
                                }
                                Text(productCount.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}
