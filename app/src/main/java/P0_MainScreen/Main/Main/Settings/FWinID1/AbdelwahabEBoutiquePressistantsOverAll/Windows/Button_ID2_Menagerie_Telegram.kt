package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientjetpack.R
import org.koin.compose.koinInject

@Composable
fun Button_ID2_Menagerie_Telegram(
    aCentralFacade: ACentralFacade = koinInject(),
    showLabels: Boolean,
    onTelegramClick: () -> Unit = {}
) {
    val repo17MessageVocaleData by aCentralFacade.repositorysMainGetter.repo17MessageVocale.datasValue.collectAsState()

    val non_Lu_Messages_Size = repo17MessageVocaleData
        .filter {
            it.etate == M17MessageVocale.Etate.ECOUTE
        }.size

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box {
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

            // Notification badge - only show if there are unread messages
            if (non_Lu_Messages_Size > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = 8.dp, y = (-8).dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (non_Lu_Messages_Size > 99) "99+" else non_Lu_Messages_Size.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
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
