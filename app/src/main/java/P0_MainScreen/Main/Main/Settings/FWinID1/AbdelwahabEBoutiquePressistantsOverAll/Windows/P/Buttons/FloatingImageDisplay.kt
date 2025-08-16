package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.P.Buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File
import kotlin.math.roundToInt

@Composable
fun FloatingImageDisplay(
    imageFile: File,
    onDismiss: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var imageSize by remember { mutableFloatStateOf(350f) }

    // Define min and max sizes for the image
    val minSize = 100f
    val maxSize = 100f
    val sizeStep = 50f

    Card(
        modifier = Modifier
            .size(imageSize.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Display the floating image
            AsyncImage(
                model = imageFile.absolutePath,
                contentDescription = "Image flotante",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        CircleShape
                    )
                    .size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Fermer image flotante",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Zoom controls (+ and - buttons)
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Zoom in button (+)
                IconButton(
                    onClick = {
                        if (imageSize < maxSize) {
                            imageSize = (imageSize + sizeStep).coerceAtMost(maxSize)
                        }
                    },
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.7f),
                            CircleShape
                        )
                        .size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agrandir image",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Zoom out button (-)
                IconButton(
                    onClick = {
                        if (imageSize > minSize) {
                            imageSize = (imageSize - sizeStep).coerceAtLeast(minSize)
                        }
                    },
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.7f),
                            CircleShape
                        )
                        .size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Réduire image",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
