package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List.List_AcheteursDeCetteProduit
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.CouleurDisplayer
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun View_AchatCouleur(
    relative_M11AchatOperation: M11AchatOperation,
    viewModel: GrossistAchatSec12FragID1_ViewModel = koinViewModel(),
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter
) {
    val relative_list_Vents = relative_M11AchatOperation
        .get_Vents_Depuit_joined_Str_keys_List_M10Vent_NonDispo_Que_Parent_Non_Trouve(
            repositorysMainGetter.repo10OperationVentCouleur.datasValue
        )
    val sumAchatQantity = relative_M11AchatOperation.sumAchatQantity  - relative_list_Vents.sumOf { it.quantity }

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
                Column {
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
                            text = "Sans ProbableVent Q: $sumAchatQantity",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    relative_list_Vents.forEach { relative_M10Vent ->
                        View_Parent_Dispo_Vent( relative_M10Vent)
                    }
                }
            }

            List_AcheteursDeCetteProduit(
                viewModel,
                relative_M11AchatOperation = relative_M11AchatOperation
            )
        }
    }
}

@Composable
private fun View_Parent_Dispo_Vent(
    relative_M10Vent: M10OperationVentCouleur,
    aCentralFacade: ACentralFacade= koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter
) {
    Card {
        val relative_linkedParent_M10Vent =
            repositorysMainGetter.find_M10OperationVentCouleur(relative_M10Vent.linked_To_M10OperationVent_KeyID)

        val relative_M3Couleur_KeyId =
            relative_linkedParent_M10Vent?.parent_M3CouleurProduit_KeyID
        val client = repositorysMainGetter.find_M2Client_By_M10Vent(
            relative_M10Vent
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(
                    color = Color.White.copy(alpha = 0.40f),
                    shape = RoundedCornerShape(10.dp)
                ),
        ) {
            if (relative_M3Couleur_KeyId != null) {
                CouleurDisplayer(keyCouleur = relative_M3Couleur_KeyId, size = 80.dp)
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = client?.nom ?: "Client inconnu",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Quantité: ${relative_M10Vent.quantity}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
        VerticalDivider()
    }
}

