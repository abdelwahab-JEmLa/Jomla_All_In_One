package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

data class Label_Datas(
    val showLabels: Boolean,
    val active_Str: String,
    val desactive_Str: String,
    val description_Functionement: String,
)  {
    companion object {
        fun get_Default(): Label_Datas {
            return Label_Datas(
                true,
                "",
                "",
                description_Functionement = "",
            )
        }
    }
}

@Composable
fun Button_9(
    viewModel: Sec9FragId1ViewId2ViewModel = koinViewModel(),
    label_Datas: Label_Datas = Label_Datas(
        true,
        "",
        "",
        description_Functionement = "",
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (label_Datas.showLabels) {
            Text(
                text = if (uiState.showDetailsExpandedPourTout) label_Datas.active_Str else label_Datas.desactive_Str,
                color = Color.Black,
                modifier = Modifier
                    .background(
                        color = if (uiState.showDetailsExpandedPourTout)
                            Color.Red else
                            Color.Green,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        FloatingActionButton(
            onClick = {
                viewModel.toggle_selectedTypeChoisi()
            },
            modifier = Modifier.size(48.dp),
            containerColor = if (uiState.showDetailsExpandedPourTout) Color.Red else Color.Green
        ) {
            Icon(
                imageVector = if (uiState.showDetailsExpandedPourTout) Icons.Default.Toll else Icons.Default.Grass,
                contentDescription = label_Datas.active_Str,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
