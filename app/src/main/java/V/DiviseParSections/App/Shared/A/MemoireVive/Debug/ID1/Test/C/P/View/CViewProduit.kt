package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.P.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.P.View.List.ViewProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ViewProduit(
    viewModel: ViewModelMainFastSearchProduitPourVent,
    product: ArticlesBasesStatsTable,
    category: CategoriesTabelle?
) {
    val getter = viewModel.getter
    val onVentData = getter.gBonVentRepository.onVentData

    val relativeVent by remember {
        mutableStateOf(
            getter.fVentCouleurOperationRepository.datasValue
                .find { it.parentBProduitInfosKeyId == product.keyID }
                ?: FCouleurVentOperationInfos(
                    parentZAppComptID = getter.zAppComptRepositoryComposable.currentAppCompt?.keyID
                        ?: "Non Definie",
                    parentHVentPeriodKeyId = ParametresAppComptNonSaved().activePeriodKeyId,
                    parentGBonVentKeyId = onVentData.keyID,
                    parentBProduitInfosKeyId = product.keyID
                )
        )
    }

    // Get all vents related to this product
    val relatedVents = remember(product.keyID) {
        getter.fVentCouleurOperationRepository.datasValue
            .filter { it.parentBProduitInfosKeyId == product.keyID }
    }

    val modifierAvecSemanticsTestTag = Modifier.semantics(mergeDescendants = true) {
        set(
            SemanticsPropertyKey("1 relativeVent"),
            relativeVent
        )
        set(
            SemanticsPropertyKey("4 onVentData"),
            onVentData
        )
    }

    Column(modifier = modifierAvecSemanticsTestTag) {
        // Use ViewProduit_T1 for the main product display
        ViewProduit_T1(
            productKeyId = product.keyID,
            vents = relatedVents
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Show availability status
        ViewDisponibilityEtates(product = product)
    }
}

@Composable
private fun ViewDisponibilityEtates(product: ArticlesBasesStatsTable) {
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
