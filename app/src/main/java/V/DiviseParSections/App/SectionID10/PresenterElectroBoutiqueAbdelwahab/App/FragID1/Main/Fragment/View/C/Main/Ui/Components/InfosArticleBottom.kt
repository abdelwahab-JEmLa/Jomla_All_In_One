package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.UiState
import org.koin.compose.koinInject
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun InfosArticleBottom(
    article: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    uiState: UiState,
    cAfficheurTelephone: Boolean,
    aA_MasterRepositorys: A_MasterRepositorysGrpProtoJuin3 = koinInject()
) {
    // Fixed: Complete the category collection logic
    val repoState = aA_MasterRepositorys.model.collectAsState()
    val categ = repoState.value?.repoStateC_CategorieProduitInfos?.modelListFlow ?: emptyList()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = "زبون جديد في طور عرض الخدمة",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        // Fixed: Complete the category finding logic
        val relatedCat = categ.find { it.id == article.idParentCategorie }

        // Display category information
     /*   Text(
            text = if (relatedCat != null) {
                "الفئة: ${relatedCat.nom} (ID: ${article.idParentCategorie} Posit = ${relatedCat.position})"
            } else {
                "الفئة: غير محددة (ID: ${article.idParentCategorie})"
            }
        )     */

        if (cAfficheurTelephone) {

            // Check if monPrixVent is greater than 0
            if (article.prixVent > 0) {
                // Row to display both prices side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Total price
                    Text(
                        text = remember {
                            val currencyFormat =
                                NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
                                    currency = Currency.getInstance("DZD")
                                }
                            currencyFormat.format(article.prixVent)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (article.nombreUniteInt > 1) {
                        Text("->")
                        Text(
                            text = remember {
                                val currencyFormat =
                                    NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
                                        currency = Currency.getInstance("DZD")
                                    }
                                val unitPrice =
                                    article.prixVent / article.nombreUniteInt

                                currencyFormat.format(unitPrice)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                Text(
                    text = "ان شاء الله نحاولو نديرولك سعر شباب",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Text(
            text = article.nom,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
