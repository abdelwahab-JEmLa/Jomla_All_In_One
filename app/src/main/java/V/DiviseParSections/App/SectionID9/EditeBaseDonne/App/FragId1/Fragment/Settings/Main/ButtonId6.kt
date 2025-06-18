package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.PRODUCTS_LISTViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LeakAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun ButtonId6(
    viewModelPRODUCTS_LIST: PRODUCTS_LISTViewModel = koinViewModel(),
    showLabels: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabels) Text("viewModelPRODUCTS_LIST.update()")
        FloatingActionButton(
            onClick = {
                viewModelPRODUCTS_LIST.update()
            },
            modifier = Modifier.size(40.dp),
            containerColor = Color.Green
        ) {
            Icon(Icons.Default.LeakAdd, "Move Categories to Catalogue", tint = Color.White)
        }
    }
}
