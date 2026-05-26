package Application4.App.Fragment.ID1.Fragment

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reproduit le style mosaïque de F1.jpg en Compose pur,
 * avec des animations infinies sur chaque photo.
 *
 * Chaque image est affichée comme une photo polaroïd inclinée.
 * Les rotations, tailles et positions reproduisent l'effet de collage.
 *
 * @param affiche  Active/désactive le composable (si false → rien)
 * @param images   Liste de 1 à 6 @DrawableRes (idéalement 5-6 pour remplir)
 */
@Composable
fun PubAbdelwahabElectroGroStore(
    affiche: Boolean,
    @DrawableRes images: List<Int>,
    modifier: Modifier = Modifier,
) {
    if (!affiche || images.isEmpty()) return

    // Configuration statique de chaque photo : position, taille, rotation cible
    data class PhotoConfig(
        val offsetX: Dp,
        val offsetY: Dp,
        val width: Dp,
        val height: Dp,
        val baseRotation: Float,      // rotation de base (style mosaïque)
        val animRotDelta: Float,      // amplitude d'oscillation en degrés
        val animScaleMin: Float,
        val animScaleMax: Float,
        val animDuration: Int,        // ms pour 1 cycle
    )

    val configs = listOf(
        // Grande photo fond-gauche (comme F1)
        PhotoConfig(-130.dp, (-20).dp, 260.dp, 190.dp, -2f,  0.8f, 1.00f, 1.03f, 6800),
        // Photo centre-haut inclinée à droite
        PhotoConfig(  30.dp, (-60).dp, 200.dp, 160.dp,  8f,  1.0f, 0.98f, 1.04f, 5500),
        // Photo droite inclinée à gauche
        PhotoConfig( 155.dp, (-30).dp, 190.dp, 155.dp, -6f,  0.9f, 1.00f, 1.03f, 7200),
        // Photo centre-bas petit format
        PhotoConfig(  10.dp,  80.dp,  170.dp, 135.dp,  4f,  1.1f, 0.99f, 1.05f, 4800),
        // Photo bas-gauche
        PhotoConfig(-120.dp,  90.dp,  155.dp, 120.dp, -3f,  0.7f, 1.00f, 1.02f, 8000),
        // Photo bas-droite
        PhotoConfig( 140.dp,  95.dp,  155.dp, 120.dp,  5f,  0.8f, 0.99f, 1.04f, 6200),
    )

    val infinite = rememberInfiniteTransition(label = "mosaic_anim")

    // Génère un animFloat par photo pour rotation
    val rotAnims = configs.mapIndexed { i, cfg ->
        infinite.animateFloat(
            initialValue = cfg.baseRotation - cfg.animRotDelta,
            targetValue  = cfg.baseRotation + cfg.animRotDelta,
            animationSpec = infiniteRepeatable(
                animation  = tween(cfg.animDuration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "rot_$i",
        )
    }

    // Génère un animFloat par photo pour scale (Ken Burns léger)
    val scaleAnims = configs.mapIndexed { i, cfg ->
        infinite.animateFloat(
            initialValue = cfg.animScaleMin,
            targetValue  = cfg.animScaleMax,
            animationSpec = infiniteRepeatable(
                animation  = tween(cfg.animDuration + 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "scale_$i",
        )
    }

    // Fond bleu foncé (comme F1)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1A2E)),
        contentAlignment = Alignment.Center,
    ) {
        // Affiche chaque photo disponible
        images.take(configs.size).forEachIndexed { i, resId ->
            val cfg      = configs[i]
            val rotation by rotAnims[i]
            val scale    by scaleAnims[i]

            Box(
                modifier = Modifier
                    .offset(x = cfg.offsetX, y = cfg.offsetY)
                    .size(cfg.width, cfg.height)
                    .graphicsLayer {
                        rotationZ    = rotation
                        scaleX       = scale
                        scaleY       = scale
                        shadowElevation = 12f
                    }
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
            text  = "✦ Abdelwahab Electro Gro Store ✦",
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
