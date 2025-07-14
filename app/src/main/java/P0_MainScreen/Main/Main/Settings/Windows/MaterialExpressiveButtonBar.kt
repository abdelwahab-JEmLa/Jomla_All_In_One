// MainActivity.kt
package com.example.materialexpressivebuttons

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MaterialExpressiveButtonBar() {
    var selectedButton by remember { mutableStateOf<String?>(null) }
    var isLikePressed by remember { mutableStateOf(false) }
    var isLikeYellow by remember { mutableStateOf(false) }
    var likeProgress by remember { mutableStateOf(0f) }

    // Animation pour le progress du bouton Like
    val animatedProgress by animateFloatAsState(
        targetValue = if (isLikePressed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        finishedListener = { progress ->
            if (progress == 1f && isLikePressed) {
                // Activation du bouton Like après 1 seconde
                selectedButton = if (selectedButton == "like") null else "like"
                isLikePressed = false
                isLikeYellow = false // Désactive l'état jaune
            }
        }
    )

    LaunchedEffect(animatedProgress) {
        likeProgress = animatedProgress
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8BBD9),
                        Color(0xFFE1BEE7)
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Material 3 Expressive",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D),
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // Container principal avec effet glassmorphism
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.8f))
                .padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bouton Favori avec clic long
                ExpressiveButton(
                    icon = Icons.Default.FavoriteBorder,
                    label = "Like",
                    isSelected = selectedButton == "like",
                    isPressed = isLikePressed,
                    isYellow = isLikeYellow,
                    progress = likeProgress,
                    onClick = {
                        // Premier clic : active l'état jaune
                        if (!isLikeYellow && selectedButton != "like") {
                            isLikeYellow = true
                        }
                    },
                    onLongPress = {
                        // Commence l'animation seulement si le bouton est jaune
                        if (isLikeYellow) {
                            isLikePressed = true
                        }
                    },
                    onRelease = {
                        if (likeProgress < 1f) {
                            isLikePressed = false
                        }
                    }
                )

                // Bouton Partager
                ExpressiveButton(
                    icon = Icons.Default.Share,
                    label = "Share",
                    isSelected = selectedButton == "share",
                    onClick = {
                        selectedButton = if (selectedButton == "share") null else "share"
                    }
                )

                // Bouton Plus (style spécial)
                ExpressiveButton(
                    icon = Icons.Default.Add,
                    label = "More",
                    isSelected = selectedButton == "more",
                    isSpecial = true,
                    onClick = {
                        selectedButton = if (selectedButton == "more") null else "more"
                    }
                )

                // Bouton Éditer
                ExpressiveButton(
                    icon = Icons.Default.Edit,
                    label = "Edit",
                    isSelected = selectedButton == "edit",
                    onClick = {
                        selectedButton = if (selectedButton == "edit") null else "edit"
                    }
                )

                // Bouton Signets
                ExpressiveButton(
                    icon = Icons.Default.BookmarkBorder,
                    label = "Save",
                    isSelected = selectedButton == "save",
                    onClick = {
                        selectedButton = if (selectedButton == "save") null else "save"
                    }
                )
            }
        }

        // Texte d'information
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "1. Cliquez sur Like pour l'activer en jaune\n2. Maintenez enfoncé 1 seconde pour l'activer en violet",
            fontSize = 14.sp,
            color = Color(0xFF666666),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun ExpressiveButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    isSpecial: Boolean = false,
    isPressed: Boolean = false,
    isYellow: Boolean = false,
    progress: Float = 0f,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {},
    onRelease: () -> Unit = {}
) {
    // Animation de scale
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else if (isPressed || isYellow) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // Animation de rotation pour certains boutons
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 360f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onLongPress()
                        tryAwaitRelease()
                        onRelease()
                    },
                    onTap = {
                        onClick()
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .scale(scale)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Fond du bouton
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = when {
                            isSpecial -> Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFEF4444),
                                    Color(0xFFEC4899)
                                )
                            )
                            isSelected -> Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF8B5CF6),
                                    Color(0xFFEC4899)
                                )
                            )
                            isYellow -> Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFBBF24),
                                    Color(0xFFF59E0B)  // Jaune plus foncé
                                )
                            )
                            else -> Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF3F4F6),
                                    Color(0xFFE5E7EB)
                                )
                            )
                        }
                    )
            )

            // Indicateur de progression pour le bouton Like
            if (label == "Like" && progress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF8B5CF6).copy(alpha = 0.3f),
                                    Color(0xFFEC4899).copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Cercle de progression
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val strokeWidth = 4.dp.toPx()
                    drawArc(
                        color = androidx.compose.ui.graphics.Color(0xFF8B5CF6),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        ),
                        size = Size(
                            size.width - strokeWidth,
                            size.height - strokeWidth
                        ),
                        topLeft = Offset(
                            strokeWidth / 2,
                            strokeWidth / 2
                        )
                    )
                }
            }

            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected || isSpecial || isYellow) Color.White else Color(0xFF374151),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = when {
                isSelected -> Color(0xFF8B5CF6)
                isYellow -> Color(0xFFF59E0B)
                else -> Color(0xFF6B7280)
            },
            fontWeight = if (isSelected || isYellow) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun MaterialExpressiveButtonsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF8B5CF6),
            secondary = Color(0xFFEC4899),
            background = Color(0xFFF9FAFB)
        ),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialExpressiveButtonsTheme {
        MaterialExpressiveButtonBar()
    }
}

// build.gradle.kts (Module: app)
/*
dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.activity:activity-compose:1.8.2'
    implementation platform('androidx.compose:compose-bom:2023.10.01')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.compose.material:material-icons-extended'
    implementation 'androidx.compose.animation:animation:1.5.4'
}
*/
