package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.AnimatedIconLottieJsonFile
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin.Partage.Views.AnimatedIconLottieJsonFile
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ControlButton(
    onClick: () -> Unit,
    icon: Any,
    contentDescription: String,
    showLabels: Boolean,
    labelText: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (icon) {
            is ImageVector -> {
                FloatingActionButton(
                    onClick = {
                        if (enabled) {
                            onClick()
                        }
                    },
                    modifier = modifier.size(40.dp),
                    containerColor = containerColor,
                ) {
                    Icon(icon, contentDescription)
                }
            }

            is LottieJsonGetterR_Raw_Icons -> {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(enabled = enabled) {
                            onClick()
                        }
                        .background(
                            color = if (enabled) containerColor else Color.Gray,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedIconLottieJsonFile(
                        ressourceXml = icon,
                        onClick = if (enabled) onClick else ({})
                    )
                }
            }

            is Int -> {
                // Support for direct resource IDs like R.raw.categ
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(enabled = enabled) {
                            onClick()
                        }
                        .background(
                            color = if (enabled) containerColor else Color.Gray,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedIconLottieJsonFile(
                        resourceId = icon,
                        onClick = if (enabled) onClick else ({})
                    )
                }
            }

            else -> {
                throw IllegalArgumentException("Unsupported icon type")
            }
        }

        if (showLabels) {
            Text(
                labelText,
                modifier = Modifier
                    .background(if (enabled) containerColor else Color.Gray)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
