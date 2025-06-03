package Views.P1.Ui.ArticlesGrid.Components.ArticleItem
import Views.P1.Ui.ArticlesGrid.AutoResizedText
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clientjetpack.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ColorIndicator(
    iconColore: String,
    modifier: Modifier = Modifier,
    onClickToOpenWindow: () -> Unit,
    imageSize: DpSize,

    ) {
    val demiSizeImage = imageSize.width>200.dp
    Box(modifier = modifier.clickable { onClickToOpenWindow() }) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ) {
            if (iconColore == "©" || iconColore == "💯"|| iconColore == "") {
                GlideImage(
                    model = R.drawable.logo,
                    contentDescription = "Logo",
                    modifier = Modifier.size(
                        if (demiSizeImage) 70.dp else 20.dp
                    )
                )
            } else {
                Text(
                    text = iconColore,
                    fontSize =  if (demiSizeImage) 45.sp else 20.sp,
                    fontWeight = FontWeight.Bold ,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ColorOverlay(
    color: ColorsArticlesTabelle,
    modifier: Modifier = Modifier,
    onClickToOpenWindow: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color name with circular background
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .matchParentSize(),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.9f))
                ) {}

                AutoResizedText(
                    text = color.nameColore,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onClickToOpenWindow() },
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
                Text(
                    text = color.iconColore,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold ,
                    modifier = Modifier.clickable { onClickToOpenWindow() },
                    color = Color.White,
                    maxLines = 1
                )
                // Fixed hand icon positioning
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (14).dp, y = 18.dp)
                        .size(60.dp)
                        .clickable { onClickToOpenWindow() }
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
