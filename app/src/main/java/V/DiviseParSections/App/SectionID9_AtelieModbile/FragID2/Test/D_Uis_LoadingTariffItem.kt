package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingTariffItem(loadingProgress: Float) {
    if (loadingProgress >= 1f) return

    ElevatedCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.size(40.dp),
                containerColor = Color.Gray
            ) {
                CircularProgressIndicator(
                    progress = {
                        loadingProgress
                    },
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                )
            }
            Text(
                "Loading... ${(loadingProgress * 100).toInt()}%", // Add percentage display
                modifier = Modifier
                    .background(Color.Gray)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
