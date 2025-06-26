package Z_CodePartageEntreApps.Modules.D.Glide.Proto

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ArticlesBasesStatsTable
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
    imageFiles: List<CalculeCouleurHandler.ProductImageInfo>,
    size: Dp?,
    qualityImage: Int,
    onLoadComplete: () -> Unit,
    actualiseSonImage: Int = 0,
    imageRefreshKey: String? = null,
    product: ArticlesBasesStatsTable?,
    enableAutoScroll: Boolean = true // FIXED: Add parameter to control auto-scroll
) {
    val pagerState = rememberPagerState(pageCount = { imageFiles.size })

    // FIXED: Auto-scroll effect now controlled by enableAutoScroll parameter
    LaunchedEffect(pagerState, imageFiles.size, enableAutoScroll) {
        if (imageFiles.size > 1 && enableAutoScroll) {
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
                    product = product
                )
            } else {
                OnImageExistPas()
            }
        }
    }
}
