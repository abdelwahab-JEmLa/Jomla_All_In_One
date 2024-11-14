package P3_DisplayeProductInfosToSeller.Ui.Main
import P1_StartupScreen.Ui.AutoResizedText
import P3_DisplayeProductInfosToSeller.Modules.ImageDisplayer
import P3_DisplayeProductInfosToSeller.Ui.CompactQuantityPicker
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clientjetpack.R
import com.example.clientjetpack.ViewModel.HeadViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ColorItem(
    modifier: Modifier,
    currentSale: SoldArticlesTabelle?,
    article: ArticlesBasesStatsTable,
    color: ColorsArticlesTabelle?,
    index: Int,
    relodeTigger: Int,
    viewModel: HeadViewModel,
    height: Dp,
) {
    var showPicker by remember {
        mutableStateOf(
            when (index) {
                0 -> currentSale?.color1SoldQuantity
                1 -> currentSale?.color2SoldQuantity
                2 -> currentSale?.color3SoldQuantity
                3 -> currentSale?.color4SoldQuantity
                else -> null
            }?.let { it > 0 } ?: false
        )
    }

    val currentQuantity = remember(index, currentSale) {
        when (index) {
            0 -> currentSale?.color1SoldQuantity
            1 -> currentSale?.color2SoldQuantity
            2 -> currentSale?.color3SoldQuantity
            3 -> currentSale?.color4SoldQuantity
            else -> null
        } ?: 0
    }

    LaunchedEffect(showPicker) {
        if (!showPicker) {
            viewModel.saveSaleTransactionToSoldAriclesList()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
            ) {
                ImageDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    article = article,
                    viewModel = viewModel,
                    indexColor = index,
                    reloadKey = relodeTigger
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {


                    AnimatedVisibility(
                        visible = !showPicker,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        color?.let { colorData ->
                            Box(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(0.6f)
                                            .wrapContentHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier.matchParentSize(),
                                            shape = CircleShape,
                                            color = Color.White.copy(alpha = 0.7f),
                                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.9f))
                                        ) {}

                                        AutoResizedText(
                                            text = colorData.nameColore,
                                            modifier = Modifier.clickable { showPicker = true },
                                            color = Color.Black,
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            maxLines = 1
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(0.4f)
                                            .wrapContentHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier.matchParentSize(),
                                            shape = CircleShape,
                                            color = Color.White.copy(alpha = 0.8f),
                                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f))
                                        ) {}


                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                                            tonalElevation = 4.dp,
                                            shadowElevation = 4.dp
                                        ) {
                                            if (colorData.iconColore == "©" || colorData.iconColore == "💯"|| colorData.iconColore == "") {
                                                GlideImage(
                                                    model = R.drawable.logo,
                                                    contentDescription = "Logo",
                                                    modifier = Modifier
                                                        .size(
                                                            38.dp
                                                        )
                                                        .clickable { showPicker = true }
                                                )
                                            } else {
                                                Text(
                                                    text = colorData.iconColore,
                                                    fontSize =  38.sp,
                                                    fontWeight = FontWeight.Bold ,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.clickable { showPicker = true }
                                                )
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .offset(x = 15.dp, y = 18.dp)
                                                .size(40.dp)
                                                .clickable { showPicker = true }
                                        ) {
                                            GlideImage(
                                                model = R.drawable.hand,
                                                contentDescription = "Click indicator",
                                                contentScale = ContentScale.Fit
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showPicker,
                modifier = Modifier.weight(0.3f),
                enter = slideInHorizontally(),
                exit = slideOutHorizontally()
            ) {
                if (showPicker && color != null) {
                    CompactQuantityPicker(
                        onClosePick = { showPicker = false },
                        colorIndex = index,
                        viewModel = viewModel,
                        initialQuantity = currentQuantity,
                        height=height
                    )
                }
            }
        }
    }
}
