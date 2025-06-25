package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview

import Z_CodePartageEntreApps.Ui.LoadingScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun B1CouleurOuGoutProduitDataBaseTestDatas(
    viewModel: B1CouleurOuGoutProduitDataBaseTestDatasViewModel = koinViewModel()
) {
    val datas = viewModel.a_CentralDatasHandlerProtoJuin9.b1CouleurOuGoutProduitDataBase_Repository.datasValue
    val uiState by viewModel.uiState.collectAsState()
    val loadingProgress = viewModel.a_CentralDatasHandlerProtoJuin9.loadingProgress ?: 0f

    // Debug logging
    LaunchedEffect(datas.size) {
        println("B1CouleurOuGoutProduitDataBaseTestDatas: Data size changed to ${datas.size}")
    }

    // Show loading screen when initial data is loading or when generating data
    if (loadingProgress < 1.0f || uiState.isGeneratingData) {
        if (uiState.isGeneratingData) {
            GenerationProgressScreen(
                currentCount = uiState.progressCount,
                totalCount = uiState.totalItems
            )
        } else {
            // Show initial loading
            LoadingScreen(loadingProgress)
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Show count of generated items
                Text(
                    text = "Total items: ${datas.size}",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (uiState.progressCount > 0) {
                    Text(
                        text = "Generated ${uiState.progressCount} color variants",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (datas.isEmpty()) {
                    // Show message when no data
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No color variants available",
                                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Tap the refresh button to generate data",
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(datas.size) { index ->
                            val data = datas[index]
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "ID: ${data.key}")
                                        Text(text = "Product: ${data.parentBProduitNom}")
                                        Text(text = "Color: ${data.nomCouleurStrSiSonImageDispo}")
                                        Text(text = "Type: ${data.aAffiche}")
                                        Text(text = "Image: ${data.nomImageFichie}")
                                        if (data.parentBProduitOldID != null) {
                                            Text(text = "Parent ID: ${data.parentBProduitOldID}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    viewModel.genereDatasDepuitParent()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Generate Data"
                )
            }
        }
    }
}

@Composable
private fun GenerationProgressScreen(
    currentCount: Int,
    totalCount: Int
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Generating Color Variants",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "$currentCount items processed",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (totalCount > 0) {
                LinearProgressIndicator(
                    progress = { currentCount.toFloat() / totalCount.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}
