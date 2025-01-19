package Views.P1.Ui.Objects
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.App.CategoriesTabelle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//CategoryHeaderECB
@Composable
fun CategoryHeader(
    category: CategoriesTabelle,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.nomCategorieInCategoriesTabele,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}
