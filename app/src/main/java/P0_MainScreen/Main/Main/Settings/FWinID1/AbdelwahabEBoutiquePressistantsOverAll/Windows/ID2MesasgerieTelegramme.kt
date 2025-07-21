package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.R

@Composable
fun ID2MesasgerieTelegramme(
    showMessageurDialog: Boolean,
    showLabels: Boolean,
    onTelegramClick: () -> Unit = {}
) {         //<--
//TODO(1): affiche un rond infof bull comm roug back blanch text contien nombre des messages non lus
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = onTelegramClick,
            modifier = Modifier.size(40.dp),
            containerColor = Color(0xFF0088CC),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_telegram),
                contentDescription = "Ouvrir Messager",
                tint = Color.White
            )
        }
        if (showLabels) {
            Text(
                "Telegram Abdelwahab",
                modifier = Modifier
                    .background(Color(0xFF0088CC))
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
