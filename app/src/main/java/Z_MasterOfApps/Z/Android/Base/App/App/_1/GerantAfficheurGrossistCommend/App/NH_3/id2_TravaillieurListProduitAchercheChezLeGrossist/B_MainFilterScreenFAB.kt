package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist

import Z_MasterOfApps.Kotlin.Model.Extension.groupedProductsPatGrossist
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.update_AllProduits
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.MaterialTheme
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
fun MainScreenFilterFAB_F2(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showButtons by remember { mutableStateOf(false) }

    val groupedProducts = viewModelInitApp._modelAppsFather.groupedProductsPatGrossist

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { showButtons = !showButtons },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = if (showButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showButtons) "Hide" else "Show"
                )
            }

            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            viewModelInitApp.extension_App1_F2.afficheProduitsPourRegleConflites =
                                !viewModelInitApp.extension_App1_F2.afficheProduitsPourRegleConflites
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = "Show Conflicts Products",
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    // Rest of the code remains unchanged...
                    groupedProducts.forEachIndexed { index, (grossist, produits) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (index > 0) {
                                FloatingActionButton(
                                    onClick = {
                                        viewModelInitApp.viewModelScope.launch {
                                            val previousGrossist = groupedProducts[index - 1].first

                                            grossist.positionInGrossistsList--
                                            previousGrossist.positionInGrossistsList++

                                            val updatedProducts =
                                                viewModelInitApp._modelAppsFather.produitsMainDataBase.map { product ->
                                                    product.apply {
                                                        bonCommendDeCetteCota?.grossistInformations?.let { currentGrossist ->
                                                            when (currentGrossist.id) {
                                                                grossist.id -> {
                                                                    currentGrossist.positionInGrossistsList--
                                                                }
                                                                previousGrossist.id -> {
                                                                    currentGrossist.positionInGrossistsList++
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            update_AllProduits(
                                                updatedProducts,
                                                viewModelInitApp
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
                                text = "${grossist.nom} (${produits.size})",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(
                                        if (viewModelInitApp
                                                ._paramatersAppsViewModelModel
                                                .telephoneClientParamaters
                                                .selectedGrossistForClientF2 == grossist.id
                                        ) Color.Blue else Color.Transparent
                                    )
                                    .padding(4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            FloatingActionButton(
                                onClick = {
                                    viewModelInitApp
                                        ._paramatersAppsViewModelModel
                                        .telephoneClientParamaters
                                        .selectedGrossistForClientF2 = grossist.id
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = Color.Black
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
