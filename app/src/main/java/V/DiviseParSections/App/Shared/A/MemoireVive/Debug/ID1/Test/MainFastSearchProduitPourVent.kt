package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel

@Preview @Composable private fun ID1ScreenPrev() { MainFastSearchProduitPourVent() }


@Composable
fun MainFastSearchProduitPourVent(
    viewModel :ViewModelMainFastSearchProduitPourVent = koinViewModel(),
    modifier: Modifier = Modifier,
    tag: String =""
) {
    val repo  = viewModel.getter.bProduitInfosRepository.datasValue

    val tagParent_ID7VentPeriod = "--${Z_AppCompt.keyModel}-${ParametresAppComptNonSaved().gerantComptKeyByParent}--${Z_AppCompt.keyModelValID7VentParent}-${ParametresAppComptNonSaved().activePeriodKeyByParent}"

    val keyCompose = SemanticsPropertyKey<String>(tagParent_ID7VentPeriod)

    Surface(

    ) {

        //<--
        //TODO(1): affiche un header affiche  ajout a mui  .semantics {
        //                set(tagParent_ID7VentPeriod,keyCompose )


        //<--
        //TODO(1): afficeh un oultilined searche text qui ce focus au start

    }
    MainList()

}



@Composable
fun MainList(modifier: Modifier = Modifier) {
    //                set(datasValue,repo.size )
      //<--
      //TODO(1): afficeh Lay comumn des produits on les trion  par  categoryGroupedSortedProducts
      //et filtre uiState searche filter la list start vide quen entre apre debonce ca affiche
}

@Composable
fun ViewProduit (modifier: Modifier = Modifier) {

}
