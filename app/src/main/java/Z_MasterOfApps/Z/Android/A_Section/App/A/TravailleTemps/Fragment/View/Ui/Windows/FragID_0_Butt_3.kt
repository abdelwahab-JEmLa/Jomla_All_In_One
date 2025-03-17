package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Ui.Windows

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun FragID_0_Butt_3(
    viewModel: Windows__ViewModel,
    showLabels: Boolean,
    labelText: String,
) {
    val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()

    ControlButton(
        onClick = {
            viewModel.toggleAbdelwahabLeGerant()
        },
        icon = if (isAbdelwahabLeGerant) Icons.Default.Person else Icons.Default.Home,
        contentDescription = if (isAbdelwahabLeGerant) "Désactiver mode administrateur" else "Activer mode administrateur",
        showLabels = showLabels,
        labelText = if (isAbdelwahabLeGerant) "Mode Admin Actif" else labelText,
        containerColor = if (isAbdelwahabLeGerant) Color(0xFFE53935) else Color(0xFF2196F3)
    )
}

