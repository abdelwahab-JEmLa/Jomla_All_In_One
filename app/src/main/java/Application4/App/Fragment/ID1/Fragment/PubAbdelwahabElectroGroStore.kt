package Application4.App.Fragment.ID1.Fragment

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
@Composable
fun PubAbdelwahabElectroGroStore(
    affiche: Boolean,
    @DrawableRes images: List<Int>,
    modifier: Modifier = Modifier,
) {
    if (!affiche || images.isEmpty()) return

    data class PhotoConfig(
        val offsetX: Dp,
        val offsetY: Dp,
        val width: Dp,
        val height: Dp,
        val baseRotation: Float,
    )

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val isTablet = screenWidthDp >= 800
    // Facteur d'échelle : 1.0 sur phone, ~1.55 sur tablette 10"
    val s = if (isTablet) 1.55f else 1.0f

    val configs = remember(s) {
        listOf(
            // Grande photo fond-gauche
            PhotoConfig((-130 * s).dp, (-20 * s).dp, (260 * s).dp, (190 * s).dp, -2f),
            // Centre-haut inclinée à droite
            PhotoConfig((30 * s).dp,  (-60 * s).dp,  (200 * s).dp, (160 * s).dp,  8f),
            // Droite inclinée à gauche
            PhotoConfig((155 * s).dp, (-30 * s).dp,  (190 * s).dp, (155 * s).dp, -6f),
            // Centre-bas petit format
            PhotoConfig((10 * s).dp,  (80 * s).dp,   (170 * s).dp, (135 * s).dp,  4f),
            // Bas-gauche
            PhotoConfig((-120 * s).dp,(90 * s).dp,   (155 * s).dp, (120 * s).dp, -3f),
            // Bas-droite
            PhotoConfig((140 * s).dp, (95 * s).dp,   (155 * s).dp, (120 * s).dp,  5f),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1A2E)),
        contentAlignment = Alignment.Center,
    ) {
        images.take(configs.size).forEachIndexed { i, resId ->
            val cfg = configs[i]

            Box(
                modifier = Modifier
                    .offset(x = cfg.offsetX, y = cfg.offsetY)
                    .size(cfg.width, cfg.height)
                    .graphicsLayer { rotationZ = cfg.baseRotation }
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .clip(RoundedCornerShape(4.dp)),
            ) {
                Image(
                    painter           = painterResource(id = resId),
                    contentDescription = null,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier.fillMaxSize(),
                )
            }
        }

        // Badge discret en bas
        Text(
            text  = " Abdelwahab Electro Gro Store ",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.50f),
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .wrapContentSize(),
        )
    }
}
