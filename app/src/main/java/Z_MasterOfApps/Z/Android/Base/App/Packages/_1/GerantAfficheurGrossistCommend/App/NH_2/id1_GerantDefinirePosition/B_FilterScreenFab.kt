package Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition

import Z_MasterOfApps.Kotlin.Model.Extension.groupedProductsPatGrossist
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
fun MainScreenFilterFAB(
    modifier: Modifier = Modifier,
    viewModelProduits: ViewModelInitApp,
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
                    viewModelProduits._modelAppsFather.groupedProductsPatGrossist
                        .forEachIndexed { index, (grossist, produits) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (index > 0) {
                                    FloatingActionButton(
                                        onClick = {
                                            viewModelProduits.viewModelScope.launch {
                                                val previousGrossist = viewModelProduits._modelAppsFather.groupedProductsPatGrossist[index - 1].first
                                                grossist.positionInGrossistsList--
                                                previousGrossist.positionInGrossistsList++
                                                update_AllProduits(
                                                    viewModelProduits.produitsMainDataBase.map { product ->
                                                        product.apply {
                                                            bonCommendDeCetteCota?.grossistInformations?.let { currentGrossist ->
                                                                when (currentGrossist.id) {
                                                                    grossist.id -> currentGrossist.positionInGrossistsList--
                                                                    previousGrossist.id -> currentGrossist.positionInGrossistsList++
                                                                }
                                                            }
                                                        }
                                                    },
                                                    viewModelProduits
                                                )
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.ExpandLess, null)
                                    }
                                }

                                Text(
                                    "${grossist.nom} (${produits.size})",
                                    modifier = Modifier
                                        .background(
                                            if (viewModelProduits._paramatersAppsViewModelModel
                                                    .telephoneClientParamaters.selectedGrossistForServeur == grossist.id
                                            ) Color(0xFF2196F3) else Color.Transparent
                                        )
                                        .padding(4.dp)
                                )

                                FloatingActionButton(
                                    onClick = {
                                        viewModelProduits._paramatersAppsViewModelModel
                                            .telephoneClientParamaters.selectedGrossistForServeur = grossist.id
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
                                    Text(produits.size.toString())
                                }
                            }
                        }
                }
            }
        }
    }
}
