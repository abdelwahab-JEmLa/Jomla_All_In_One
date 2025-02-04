package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id3_AfficheurDesProduitsPourLeColecteur

import Z_MasterOfApps.Kotlin.Model.Extension.groupedProductsParClients
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MainScreenFilterFAB_F3(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp,
) {
    var offset by remember { mutableStateOf(IntOffset(0, 0)) }
    var showButtons by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .offset { offset }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offset = IntOffset(
                            (offset.x + dragAmount.x).roundToInt(),
                            (offset.y + dragAmount.y).roundToInt()
                        )
                    }
                },
            horizontalAlignment = Alignment.End
        ) {
            // Toggle FAB
            FloatingActionButton(
                onClick = { showButtons = !showButtons },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (showButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showButtons) "Hide" else "Show"
                )
            }

            // Client list
            AnimatedVisibility(visible = showButtons) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    viewModelInitApp._modelAppsFather.groupedProductsParClients
                        .forEachIndexed { index, (client, products) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                // Up button for reordering
                                if (index > 0) {
                                    FloatingActionButton(
                                        onClick = {
                                            viewModelInitApp.viewModelScope.launch {
                                                viewModelInitApp.extensionVMApp1FragmentId_3.upButton(index)
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.ExpandLess, "Move up")
                                    }
                                }

                                // Client info
                                Text(
                                    "${client.nom} (${client.id})",
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (viewModelInitApp._paramatersAppsViewModelModel
                                                    .phoneClientSelectedAcheteur == client.id
                                            ) MaterialTheme.colorScheme.primaryContainer
                                            else Color.Transparent
                                        )
                                        .padding(4.dp)
                                )

                                // Selection FAB
                                val color = try {
                                    Color(android.graphics.Color.parseColor(
                                        client.statueDeBase.couleur.let {
                                            if (it.startsWith("#")) it else "#$it"
                                        }
                                    ))
                                } catch (e: Exception) {
                                    Color.Red
                                }

                                FloatingActionButton(
                                    onClick = {
                                        viewModelInitApp.extensionVMApp1FragmentId_3.clientIDAuFilter= client.id
                                    },
                                    modifier = Modifier.size(48.dp),
                                    containerColor = color
                                ) {
                                    Text(
                                        products.size.toString(),
                                        color = if (color.red * 0.299 + color.green * 0.587 +
                                            color.blue * 0.114 > 0.5f
                                        ) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}
