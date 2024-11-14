package P3_DisplayeProductInfosToSeller.Ui.Objects
import a_RoomDB.ArticlesBasesStatsTable
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
 fun ColumnScope.Details(
    isDetailsVisible: Boolean,
    article: ArticlesBasesStatsTable
) {
    AnimatedVisibility(
        visible = isDetailsVisible,
        enter = fadeIn() + expandVertically(),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                // Price Section
                InfoRow(
                    label = "السعر للوحدة",
                    value = "${article.monPrixVent} ",
                    unite = "دج"
                )

                InfoRow(
                    label = "السعر بالجملة",
                    value = "${
                        String.format(
                            "%.2f",
                            article.monPrixVent / article.nmbrUnite.toFloat()
                        )
                    } ",
                    unite = "دج"
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                // Units Information
                InfoRow(
                    label = "عدد الحبات",
                    value = "${article.nmbrUnite} ",
                    unite = "وحدة"
                )

                // Carton Information
                InfoRow(
                    label = "في الكرتون",
                    value = "${article.nmbrCaron} ",
                    unite = "علبة"
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    unite: String,

    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = unite,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )

        Icon(
            Icons.Default.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsSectionPreview() {
    MaterialTheme {
        ProductNameSection(
            ArticlesBasesStatsTable(
                nomArticleFinale = "Product Name",
                nomArab = "اسم المنتج",
                nmbrUnite = 12,
                nmbrCaron = 24,
                monPrixVent = 1200.0
            )
        )
    }
}
@Composable
private fun ProductNameSection(article: ArticlesBasesStatsTable) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product Name Card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = article.nomArticleFinale,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                if (article.nomArab.isNotEmpty()) {
                    Text(
                        text = article.nomArab,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

