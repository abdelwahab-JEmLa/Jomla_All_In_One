package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

data class QuantityButtonAnimations(
    val scale: Float,
    val backgroundColor: Color,
    val textColor: Color
)

// Separated animation functions
@Composable
fun animateQuantityButtonScale(isSelected: Boolean): State<Float> {
    return animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 400f
        ),
        label = "quantity_button_scale"
    )
}

@Composable
fun animateQuantityButtonBackgroundColor(isSelected: Boolean): State<Color> {
    return animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = spring(dampingRatio = 0.8f),
        label = "quantity_button_color"
    )
}

@Composable
fun animateQuantityButtonTextColor(isSelected: Boolean): State<Color> {
    return animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = spring(dampingRatio = 0.8f),
        label = "quantity_text_color"
    )
}

// Combined animation composable for easier use
@Composable
fun rememberQuantityButtonAnimations(isSelected: Boolean): QuantityButtonAnimations {
    val scale by animateQuantityButtonScale(isSelected)
    val backgroundColor by animateQuantityButtonBackgroundColor(isSelected)
    val textColor by animateQuantityButtonTextColor(isSelected)

    return remember(scale, backgroundColor, textColor) {
        QuantityButtonAnimations(
            scale = scale,
            backgroundColor = backgroundColor,
            textColor = textColor
        )
    }
}
