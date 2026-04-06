package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Tex

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Modules.Loading_Datas.Init.A_MasterRepositorysGrpProtoJuin3
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import V.DiviseParSections.App.Shared.ViewModel.UiState
import org.koin.compose.koinInject

@Composable
fun InfosArticleBottom(
    article: M01Produit,
    modifier: Modifier = Modifier,
    uiState: UiState,
    cAfficheurTelephone: Boolean,
    aA_MasterRepositorys: A_MasterRepositorysGrpProtoJuin3 = koinInject()
) {
    val repoState = aA_MasterRepositorys.model.collectAsState()
    val categ = repoState.value?.repoStateC_CategorieProduitInfos?.modelListFlow ?: emptyList()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Compact_Affiche_Tariffs(cAfficheurTelephone, article)

        Text(
            text = article.nom,
            color = Color.Red,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

