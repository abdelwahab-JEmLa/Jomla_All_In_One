package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay

@Composable
fun MultipleImagesDisplay(
    imageFiles: List<ProductImageInfo>,
    size: Dp?,
    qualityImage: Int,
    onLoadComplete: () -> Unit,
    actualiseSonImage: Int = 0,
    imageRefreshKey: String? = null, // FIXED: Accept external refresh key
    product: A_ProduitInfosTest?
) {
    val pagerState = rememberPagerState(pageCount = { imageFiles.size })

    // Auto-scroll effect
    LaunchedEffect(pagerState, imageFiles.size) {
        if (imageFiles.size > 1) {
            while (true) {
                delay(3000) // 3 seconds delay
                val nextPage = (pagerState.currentPage + 1) % imageFiles.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(state = pagerState) { page ->
            val imageInfo = imageFiles.getOrNull(page)
            if (imageInfo != null) {
                SingleImageDisplay(
                    imageInfo = imageInfo,
                    qualityImage = qualityImage,
                    onLoadComplete = if (page == 0) onLoadComplete else { {} },
                    actualiseSonImage = actualiseSonImage,
                    imageRefreshKey = imageRefreshKey,
                    product = product // FIXED: Pass refresh key to SingleImageDisplay
                )
            } else {
                OnImageExistPas()
            }
        }
    }
}
