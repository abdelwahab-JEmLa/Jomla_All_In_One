package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.P.View

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ViewDisponibilityEtates(product: ArticlesBasesStatsTable) {
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
