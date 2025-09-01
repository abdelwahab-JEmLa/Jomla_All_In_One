package P0_MainScreen.Main.Main.Settings.Windows.g

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusIndicator(
    status: M8BonVent.EtateActuellementEst,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> Color.Green
        M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI -> Color.Blue
        M8BonVent.EtateActuellementEst.Cible -> Color.Red
        M8BonVent.EtateActuellementEst.Bloque_Probleme -> Color.Red
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT -> Color(0xFFFFD700)
        else -> Color.Gray
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .background(color = color, shape = CircleShape)
    )
}
