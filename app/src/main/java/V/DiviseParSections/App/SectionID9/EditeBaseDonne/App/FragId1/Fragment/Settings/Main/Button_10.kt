package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.GetApp
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
fun Button_10(
    viewModel: Sec9FragId1ViewId2ViewModel = koinViewModel(),
    button_State: Button_State = Button_State(),
    onClick:  () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (button_State.showLabels) {
            Text(
                text = button_State.textLable,
                color = Color.Black,
                modifier = Modifier
                    .background(
                        color = if (button_State.its_Active)
                            Color.Red else
                            Color.Green,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        FloatingActionButton(
            onClick = {
                onClick()
            },
            modifier = Modifier.size(48.dp),
            containerColor = if (button_State.its_Active) Color.Green else Color.Gray
        ) {
            Icon(
                imageVector = if (button_State.its_Active) Icons.Default.GetApp else Icons.Default.DeviceUnknown,
                contentDescription = button_State.active_Str,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
