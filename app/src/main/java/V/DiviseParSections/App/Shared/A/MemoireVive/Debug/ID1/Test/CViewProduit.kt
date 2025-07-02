package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ViewProduit(
    viewModel: ViewModelMainFastSearchProduitPourVent,
    product: ArticlesBasesStatsTable,
    category: CategoriesTabelle?,
    modifier: Modifier = Modifier
) {
    val getter = viewModel.getter
    val  ventOpList = getter.fVentCouleurOperationRepository.datasValue

    val parentHVentPeriodKeyId = ParametresAppComptNonSaved().activePeriodKeyByParent
    val onVentData = getter.gBonVentRepository.onVentData
    val parentGBonVentKeyId = onVentData.keyID
    val parentBProduitInfosKeyId = product.keyID

    val relativeVent by remember { mutableStateOf(
        ventOpList.find { it.parentBProduitInfosKeyId== parentBProduitInfosKeyId }
            ?: FCouleurVentOperationInfos(
                parentHVentPeriodKeyId = parentHVentPeriodKeyId,
                parentGBonVentKeyId = parentGBonVentKeyId,
                parentBProduitInfosKeyId= parentBProduitInfosKeyId
            )
    ) }
    val modifierSemanticsTestTag=  Modifier.semantics(mergeDescendants = true) {
        set(
            SemanticsPropertyKey("1parentHVentPeriodKeyId"),
            relativeVent.parentHVentPeriodKeyId
        )
        set(
            SemanticsPropertyKey("2parentGBonVentKeyId"),
            relativeVent.parentGBonVentKeyId
        )
        set(
            SemanticsPropertyKey("3 parentBProduitInfosKeyId"),
            relativeVent.parentBProduitInfosKeyId
        )
        set(
            SemanticsPropertyKey("4 onVentData"),
            onVentData
        )

    }

    Card(
        modifierSemanticsTestTag,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
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
