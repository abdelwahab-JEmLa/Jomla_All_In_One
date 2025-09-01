package P0_MainScreen.Main.Main.Settings.Windows.g

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
 fun TransitionCard(
    previousBon: M8BonVent,
    currentBon: M8BonVent,
    modifier: Modifier = Modifier
) {
    val transitionDurationMillis = currentBon.creationTimestamps - previousBon.confirmeCommande_TimeTamp
    val totalSeconds = (transitionDurationMillis / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val transitionDuration = "${minutes} min et ${seconds} sec"
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2196F3).copy(alpha = 0.1f) // Purple background
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Transition icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFF9C27B0).copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsMotorsports,
                    contentDescription = null,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Transition info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "التنقل وقته",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF9C27B0),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = transitionDuration,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF9C27B0),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Arrow indicator
            Icon(
                imageVector = Icons.Default.SportsMotorsports,
                contentDescription = null,
                tint = Color(0xFF9C27B0).copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
