package V.DiviseParSections.App.Z_Learn.z_Learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientjetpack.R

// Alternative: Preview avec une image par défaut en utilisant Image au lieu de GlideImage
@Preview(showBackground = true)
@Composable
fun ColorIndicatorPreviewWithDefaultHand() {
    MaterialTheme {
        Box {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "👍",
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (20).dp, y = 25.dp)
                    .size(38.dp)
            ) {
                // Using standard Image composable for preview
                Image(
                    painter = painterResource(id = R.drawable.hand),
                    contentDescription = "Click indicator",
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
