package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ViewProduit(
    parentSemanticsInfo: Pair<SemanticsPropertyKey<String>, String>,
    product: ArticlesBasesStatsTable,
    category: CategoriesTabelle?,
    modifier: Modifier = Modifier
) {
    val semanticsInfo = parentSemanticsInfo.copy(
        second = parentSemanticsInfo.second + "--" + ArticlesBasesStatsTable.KeyTagModel + "-" + product.nom.withOutFireBaseInvalidCharacters()
    )

    Card(
        modifier
        .semantics {
            set(semanticsInfo.first, semanticsInfo.second)
        }, elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Text(
                product.nom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (product.nomArab.isNotEmpty()) {
                Text(
                    product.nomArab,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Catégorie: ${category?.nom ?: "Non définie"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Prix: ${product.prixVent} DA",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = when (product.disponibilityEtates) {
                        DisponibilityEtates.DISPO ->
                            MaterialTheme.colorScheme.primary

                        DisponibilityEtates.NON_DISPO ->
                            MaterialTheme.colorScheme.error

                        DisponibilityEtates.PETITE_PROBABILITY ->
                            MaterialTheme.colorScheme.tertiary
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        product.disponibilityEtates.nomArabe,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
