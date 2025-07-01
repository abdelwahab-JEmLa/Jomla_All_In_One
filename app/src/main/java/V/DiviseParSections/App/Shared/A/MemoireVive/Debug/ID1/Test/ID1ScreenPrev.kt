package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.Z_AppCompt
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview @Composable private fun ID1ScreenPrev() { ID1Screen() }


@Composable
fun ID1Screen(
    modifier: Modifier = Modifier,
    tag: String =""
) {
    val tagParent_ID7VentPeriod = "--${Z_AppCompt.keyModel}-${ParametresAppComptNonSaved().gerantComptKeyByParent}--${Z_AppCompt.keyModelValID7VentParent}-${ParametresAppComptNonSaved().activePeriodKeyByParent}"

    Surface(

    ) {
        //<--
        //TODO(1): affiche un header

        //<--
        //TODO(1): afficeh un oultilined searche text qui ce focus au start
    }

}

@Composable
fun MainList(modifier: Modifier = Modifier) {

      //<--
      //TODO(1): afficeh lazyGrid des produits on les trion  par  ...
      //et filtre uiState searche filter la list start vide quen entre apre debonce ca affiche
}

@Composable
fun MainItem (modifier: Modifier = Modifier) {
            //<--
            //TODO(1): affiche l image avec
}
