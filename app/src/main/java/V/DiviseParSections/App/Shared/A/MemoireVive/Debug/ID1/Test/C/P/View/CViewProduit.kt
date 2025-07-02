package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.P.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.P.View.List.ViewProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ViewProduit(
    viewModel: ViewModelMainFastSearchProduitPourVent,
    product: ArticlesBasesStatsTable,
) {

    Column{
        ViewProduit_T1(
            productKeyId = product.keyID,
        )

        Spacer(modifier = Modifier.height(8.dp))

        ViewDisponibilityEtates(product = product)
    }
}

