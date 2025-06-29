// File: ModernToast.kt
package V.DiviseParSections.App.Shared.Modules.Ui.A.UI

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

enum class ToastType {
    SUCCESS, WARNING, INFO, ERROR
}

data class ToastData(
    val message: String,
    val type: ToastType = ToastType.INFO,
    val duration: Long = 3000L
)

@Composable
fun ModernToastMessage(
    toastData: ToastData?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    toastData?.let { data ->
        LaunchedEffect(data) {
            delay(data.duration)
            onDismiss()
        }

        val (backgroundColor, icon) = when (data.type) {
            ToastType.SUCCESS -> Color(0xFF10B981) to Icons.Default.CheckCircle
            ToastType.WARNING -> Color(0xFFEF4444) to Icons.Default.Warning
            ToastType.INFO -> Color(0xFF3B82F6) to Icons.Default.Info
            ToastType.ERROR -> Color(0xFFEF4444) to Icons.Default.Warning
        }

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                    scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(durationMillis = 300)
                    ),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) +
                    scaleOut(
                        targetScale = 0.8f,
                        animationSpec = tween(durationMillis = 300)
                    ),
            modifier = modifier.zIndex(999f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(32.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = data.message,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
