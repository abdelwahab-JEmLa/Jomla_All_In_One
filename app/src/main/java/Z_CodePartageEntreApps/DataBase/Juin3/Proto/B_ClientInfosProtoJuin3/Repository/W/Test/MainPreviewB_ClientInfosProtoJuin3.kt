package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.W.Test

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun MainPreviewB_ClientInfosProtoJuin3(
    viewModel: B_ClientInfosProtoJuin3PreviewViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val datas by remember(uiState.B_ClientInfosProtoJuin3List) { mutableStateOf(uiState.B_ClientInfosProtoJuin3List) }
    val progress = uiState.mainLoadingProgress

    val sortedDatas = remember(datas) {
        datas.sortedBy { it.id }
    }

    // Observer les changements dans les données pour détecter les mises à jour
    LaunchedEffect(datas.size) {
        if (datas.isNotEmpty()) {
            Log.d("MainPreview", "🔄 Base de données mise à jour!")
            Log.d("MainPreview", "📊 Nombre d'enregistrements: ${datas.size}")
            Log.d("MainPreview", "📈 Progression: $progress")

            // Afficher l'état actuel de la BD
            viewModel.masterRepositorys.b_ClientInfosProtoJuin3Repository.logCurrentDatabaseState()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Indicateur de mise à jour
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("📊 Données: ${datas.size}")
            Text(
                text = if (progress == 1.0f) "✅ À jour" else "🔄 Mise à jour...",
                color = if (progress == 1.0f) Color.Green else Color.Red
            )
        }

        if (progress > 0f && progress < 1f) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Boutons de test
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    Log.d("MainPreview", "🧪 Test Firebase Listener - déclenchement")
                    viewModel.masterRepositorys.b_ClientInfosProtoJuin3Repository
                        .testTriggerUpdateFbParTimestampsListener()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("🧪 Test Firebase")
            }

            Button(
                onClick = {
                    Log.d("MainPreview", "📊 Affichage état BD")
                    viewModel.masterRepositorys.b_ClientInfosProtoJuin3Repository
                        .logCurrentDatabaseState()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("📊 État BD")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(sortedDatas) { data ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = data.nom,
                                modifier = Modifier.weight(1f)
                            )
                            Text(data.id.toString())
                        }

                        Text(
                            text = "🔄 Dernière MAJ: ${data.dernierFireBaseUpdateTimestamps}",
                            color = Color.Gray
                        )

                        // Indicateur pour les données de test
                        if (data.keyFireBase.contains("TEST_")) {
                            Text(
                                text = "🧪 DONNÉE DE TEST",
                                color = Color.Blue
                            )
                        }

                        // Indicateur pour les clients temporaires
                        if (data.cUnClientTemporaire) {
                            Text(
                                text = "⏰ Client temporaire",
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}
