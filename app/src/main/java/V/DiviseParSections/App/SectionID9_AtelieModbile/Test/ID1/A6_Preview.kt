package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun Test2Prev() {
    val idProduitChahrazed = 849L
    val idTP2 = 859L
    TariffsButtons_TestID2(
        filterProductId = idProduitChahrazed,
        filterBonId = 1,
        fermeDialog = { },
        cLenceDepuitDialogeAchate = true,
    )
}



