package Application4.App.Main.A.Navigation.Component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.R

private const val TAG = "FAB_BUTTON"

@Composable
fun FabButton_newProto(
    showWarningState: Boolean,
    isFabVisible: Boolean,
    its_Targeted_Frag: Boolean,
    onToggleFabVisibility: () -> Unit,
    onShowDropdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d(TAG, "FabButton composé — showWarningState=$showWarningState | isFabVisible=$isFabVisible | its_Targeted_Frag=$its_Targeted_Frag")

    Surface(
        modifier = modifier
            .offset(y = (-28).dp)
            .size(56.dp),
        shape = CircleShape,
    ) {
        Box {
            if (showWarningState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFDC2626),
                                    Color(0xFFB91C1C)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable {
                            Log.d(TAG, "⚡ CLICK — branche WARNING | its_Targeted_Frag=$its_Targeted_Frag")
                            when (its_Targeted_Frag) {
                                false -> {
                                    Log.d(TAG, "→ appel onToggleFabVisibility()")
                                    onToggleFabVisibility()
                                }
                                true -> {
                                    Log.d(TAG, "→ appel onShowDropdown()")
                                    onShowDropdown()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                Log.d(TAG, "FabButton — branche LOGO (showWarningState=false)")
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            Log.d(TAG, "⚡ CLICK — branche LOGO | its_Targeted_Frag=$its_Targeted_Frag")
                            when (its_Targeted_Frag) {
                                false -> {
                                    Log.d(TAG, "→ appel onToggleFabVisibility()")
                                    onToggleFabVisibility()
                                }
                                true -> {
                                    Log.d(TAG, "→ appel onShowDropdown()")
                                    onShowDropdown()
                                }
                            }
                        },
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = if (isFabVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle FAB",
                    modifier = Modifier.align(Alignment.Center),
                    tint = Color.White
                )
            }
        }
    }
}
