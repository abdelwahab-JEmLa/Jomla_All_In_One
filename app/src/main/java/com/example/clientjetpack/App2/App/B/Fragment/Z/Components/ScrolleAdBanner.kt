package com.example.clientjetpack.App2.App.B.Fragment.Z.Components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.R
import kotlinx.coroutines.delay

@Composable
fun ScrolleAdBanner(
    modifier: Modifier = Modifier,
    onBannerClick: (Int) -> Unit = {}, // Default empty handler
    onClickImageToShowControles: () -> Unit
) {
    var currentBannerIndex by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val cardWidth = with(density) { 350.dp.toPx() }
    val totalCards = 3

    // Custom auto-scroll behavior
    LaunchedEffect(Unit) {
        while (true) {
            // Forward scroll (left to right)
            while (currentBannerIndex < totalCards - 1) {
                delay(1500)

                // Calculate steps for forward scroll
                val totalSteps = 35
                val stepSize = cardWidth / totalSteps

                // Smooth scroll to card
                for (step in 0 until totalSteps) {
                    val nextPosition = (currentBannerIndex * cardWidth) + (step * stepSize)
                    scrollState.scrollTo(nextPosition.toInt())
                    delay(10) // 10ms delay between each step
                }

                currentBannerIndex++
            }

            // At this point we're at the last card
            delay(3000) // Pause before returning

            // Reverse scroll (right to left)
            val totalSteps = 35
            val maxScroll = (totalCards - 1) * cardWidth
            val stepSize = maxScroll / totalSteps

            // Smooth scroll back to start
            for (step in 0 until totalSteps) {
                val nextPosition = maxScroll - (step * stepSize)
                scrollState.scrollTo(nextPosition.toInt())
                delay(10) // 10ms delay between each step
            }

            // Reset position
            currentBannerIndex = 0
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Map images to their corresponding category IDs
        val bannerData = listOf(
            BannerItem(
                R.drawable.logo,
                "Confiseries"
            ), // This will navigate to categoryHeaderConfiseries
            BannerItem(R.drawable.logo, "cosmitiques"),          // No specific navigation
            BannerItem(
                R.drawable.logo,
                "atay_moukassarat"
            )           // No specific navigation
        )

        bannerData.forEachIndexed { index, (imageRes, categoryName) ->
            Card(
                modifier = Modifier
                    .width(320.dp)
                    .height(320.dp)
                    .clickable {
                        // Pass the banner index to the click handler
                        onBannerClick(index)
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Banner image ${index + 1}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onClickImageToShowControles()
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// Data class to hold banner image and target category
private data class BannerItem(
    val imageResId: Int,
    val targetCategoryName: String?
)

@Composable
fun scrollToCategory(
    categoryId: Long,
    targetCategoryId: MutableState<Long?>,
    logTag: String = "ScrollDebug"
) {
    Log.d(logTag, "Attempting to scroll to category ID: $categoryId")
    targetCategoryId.value = categoryId
}
