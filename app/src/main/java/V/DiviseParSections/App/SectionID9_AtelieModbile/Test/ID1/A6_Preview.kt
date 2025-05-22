package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun Test2Prev() {
    val idProduitChahrazed = 849
    val idTP2 = 859
    TariffsButtons_TestID2(
        filterProductId = idProduitChahrazed.toLong(),
        filterBonId = 1,
        cLenceDepuitDialogeAchate = true,
        fermeDialog = { },
    )
}



