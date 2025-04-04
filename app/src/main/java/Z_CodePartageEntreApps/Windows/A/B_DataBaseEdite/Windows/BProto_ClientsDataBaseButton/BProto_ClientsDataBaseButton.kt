package Z_CodePartageEntreApps.Windows.A.B_DataBaseEdite.Windows.BProto_ClientsDataBaseButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun BProto_ClientsDataBaseButton(
    viewModel: ViewModel_BProto_ClientsDataBase = koinViewModel()
) {
    var displayeButtonsFunctionRelated by remember { mutableStateOf(false) }
    var updateProgress by remember { mutableStateOf(0f) }

    Button(
        onClick = {
            displayeButtonsFunctionRelated = !displayeButtonsFunctionRelated
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (updateProgress > 0)
                "B_ClientDataBase (${(updateProgress * 100).toInt()}%)"
            else "B_ClientDataBase"
        )
    }

    AnimatedVisibility(
        visible = displayeButtonsFunctionRelated,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        LazyColumn {
            item {
                ButFun_1_populateB_ClientDataBaseParSonAncien(
                    viewModel = viewModel,
                    onProgressUpdate = { progress ->
                        updateProgress = progress
                    },
                    nameFunciotn = "populateModelearSonAncien()"
                )
            }


        }
    }
}
