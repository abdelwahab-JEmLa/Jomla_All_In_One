package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.W.Test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun LoadingScreenB_ClientProtoJuin3(loadingProgress: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Enhanced circular progress with custom styling
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Background circle
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(120.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 8.dp,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                )

                // Progress circle with animation
                if (loadingProgress > 0f) {
                    CircularProgressIndicator(
                        progress = { loadingProgress },
                        modifier = Modifier.size(120.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 8.dp,
                        trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(120.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 8.dp
                    )
                }

                // Percentage text in center
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (loadingProgress > 0f) {
                        Text(
                            text = "${(loadingProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "...",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress message with better styling
            Text(
                text = getLoadingMessage(loadingProgress),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtle secondary text
            Text(
                text = "Veuillez patienter...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getLoadingMessage(progress: Float): String {
    return when {
        progress == 0f -> "Initialisation..."
        progress <= 0.3f -> "Préparation des données..."
        progress <= 0.6f -> "Récupération depuis Firebase..."
        progress <= 0.8f -> "Synchronisation locale..."
        progress < 1.0f -> "Configuration des listeners..."
        else -> "Finalisation..."
    }
}
