package Application4.App.Fragment.View.ViewS

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Header component with delete button that requires double-click with 4-second countdown
 *
 * Features:
 * - First click: Starts 4-second countdown
 * - Second click: Confirms deletion
 * - Right-side click during countdown: Cancels operation
 * - Auto-resets if countdown expires
 *
 * Usage:
 * DeleteProductHeader(
 *     productName = product.nom,
 *     onDelete = {
 *         // Your delete logic here
 *         repositorysMainGetter.repoM1Produit.deleteData(product)
 *     }
 * )
 */
@Composable
fun DeleteProductHeader(
    productName: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCountdownActive by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableIntStateOf(4) }
    var progress by remember { mutableFloatStateOf(1f) }

    // Smooth countdown animation
    LaunchedEffect(isCountdownActive) {
        if (isCountdownActive) {
            countdownSeconds = 4
            progress = 1f
            val totalMillis = 4000f
            val step = 50L // Update every 50ms for smooth animation
            var elapsed = 0L

            while (elapsed < totalMillis && isCountdownActive) {
                delay(step)
                elapsed += step
                progress = 1f - (elapsed / totalMillis)
                countdownSeconds = ((totalMillis - elapsed) / 1000).toInt() + 1
            }

            // Reset if countdown expires
            if (elapsed >= totalMillis) {
                isCountdownActive = false
                progress = 1f
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    !isCountdownActive -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                }
            )
            .clickable {
                if (!isCountdownActive) {
                    // First click: Start countdown
                    isCountdownActive = true
                } else {
                    // Second click during countdown: Execute deletion
                    isCountdownActive = false
                    onDelete()
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = when {
                !isCountdownActive -> "🗑️ Supprimer \"$productName\""
                else -> "👆 Cliquer pour confirmer"
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = when {
                !isCountdownActive -> MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.colorScheme.onError
            },
            modifier = Modifier.weight(1f)
        )

        if (isCountdownActive) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular countdown indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .drawBehind {
                            val strokeWidth = 4.dp.toPx()
                            // Background circle
                            drawCircle(
                                color = Color.White.copy(alpha = 0.3f),
                                style = Stroke(width = strokeWidth)
                            )
                            // Progress arc
                            drawArc(
                                color = Color.White,
                                startAngle = -90f,
                                sweepAngle = 360f * progress,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$countdownSeconds",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onError
                    )
                }

                // Cancel button - more appealing
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onError.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { isCountdownActive = false }
                        .background(MaterialTheme.colorScheme.onError.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}
