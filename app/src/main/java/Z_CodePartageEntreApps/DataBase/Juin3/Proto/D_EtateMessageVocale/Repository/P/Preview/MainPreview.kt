package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.P.Preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun MainPreviewD_EtateMessageVocale(
    viewModel: D_EtateMessageVocalePreviewViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val datas by remember(uiState.mainList) { mutableStateOf(uiState.mainList) }
    val progress = uiState.mainLoadingProgress

    val sortedDatas = remember(datas) {
        datas.sortedBy { it.id }
    }

    LaunchedEffect(Unit) {
        println("Repository: ${datas.size} items, progress: $progress")
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("datas: ${datas.size}")

        if (progress > 0f && progress < 1f) {
            LinearProgressIndicator(
                progress = { progress },
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sortedDatas) { data ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = data.nom.name,
                            modifier = Modifier.weight(1f)
                        )

                        Text(data.id.toString())
                    }
                }
            }
        }
    }
}
