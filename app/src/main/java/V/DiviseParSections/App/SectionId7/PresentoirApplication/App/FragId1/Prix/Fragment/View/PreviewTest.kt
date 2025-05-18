package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.View

import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun PreviewTest() {
    FragmentMain(
        produitSelectioneDuAncienDataBase= ArticlesBasesStatsTable(
            idArticle = 1,
            nomArticleFinale = "Non",
            monPrixVent = 100.00
        )
    )
}
