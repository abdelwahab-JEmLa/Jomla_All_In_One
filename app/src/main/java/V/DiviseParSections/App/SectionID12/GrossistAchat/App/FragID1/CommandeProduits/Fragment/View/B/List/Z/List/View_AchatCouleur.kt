package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List.List_AcheteursDeCetteProduit
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.CouleurDisplayer
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Preview
@Composable
private fun View_AchatCouleurPrv(
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
) {
   val data = repositorysMainGetter.repo11AchatOperation.datasValue.first()
    View_AchatCouleur(relative_M11AchatOperation =data)
}

@Composable
fun View_AchatCouleur(
    relative_M11AchatOperation: M11AchatOperation,
    viewModel: GrossistAchatSec12FragID1_ViewModel = koinViewModel(),
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter
) {
    Card(
        modifier = Modifier.background(Color.Red)
    ) {
        Column {
            Box {
                val couleurKey = relative_M11AchatOperation.parent_M3CouleurProduit_KeyID
                if (couleurKey.isNotEmpty()) {
                    CouleurDisplayer(keyCouleur = couleurKey)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                color = Color.Gray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            text = "Couleur non disponible",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.70f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                ) {
                    Text(
                        text = "Qté: ${relative_M11AchatOperation.sumAchatQantity}",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(4.dp)
                    )
                }

                val relative_list_Vents = relative_M11AchatOperation
                    .get_Vents_Depuit_joined_Str_keys_List_M10Vent_NonDispo_Que_Parent_Non_Trouve(
                        repositorysMainGetter.repo10OperationVentCouleur.datasValue
                    )

                relative_list_Vents.forEach {
                    ElevatedCard() {
                        Text(it.getDebugInfos())
                    }
                    HorizontalDivider()
                }
            }

            List_AcheteursDeCetteProduit(viewModel, relative_M11AchatOperation)
        }
    }
}
