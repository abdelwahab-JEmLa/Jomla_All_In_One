package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List.List_AcheteursDeCetteProduit
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List.View.Parent_Dispo_Vent_View
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.empty_If_Null
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val list_NonDispo_M10Vent = relative_M11AchatOperation
        .get_Vents_Depuit_joined_Str_keys_List_M10Vent_NonDispo_Que_Parent_Non_Trouve(
            repositorysMainGetter.repo10OperationVentCouleur.datasValue
        )
    val sumAchatQantity =
        relative_M11AchatOperation.sumAchatQantity - list_NonDispo_M10Vent.sumOf { it.quantity }

    Card(
        modifier = Modifier.background(Color.Red)
    ) {
        Column {
            Box {
                val couleurKey = relative_M11AchatOperation.parent_M3CouleurProduit_KeyID
                val couleur_Par_Nom = null

                if (couleurKey.isNotEmpty() && couleurKey.isNotBlank()) {
                    val couleurExists =
                        repositorysMainGetter.find_M3CouleurInfos_By_KeyID(couleurKey) != null
                    if (couleurExists) {
                        CouleurDisplayer(keyCouleur = couleurKey)
                    } else if (couleur_Par_Nom != null) {


                    } else {
                        Box(
                            modifier = Modifier
                                .getSemanticsTag(
                                    relative_M11AchatOperation,
                                    "relative_M11AchatOperation"
                                )
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(
                                    color = Color.Gray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(
                                text = "Couleur introuvable (ID: ${couleurKey.takeLast(8)})",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                } else {
                    // Original fallback for empty/blank keys
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
                    if (sumAchatQantity != 0) {
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
                    }

                    val map_SiNonDispoM3CouleurInfos_To_List_SNC_M10Vent = list_NonDispo_M10Vent
                        .groupBy {
                            repositorysMainGetter.find_M3CouleurInfos_By_KeyID(it.siNonDispoParentM10Vent_it_parent_M3CouleurInfos_KeyId)
                        }

                    map_SiNonDispoM3CouleurInfos_To_List_SNC_M10Vent.forEach { (m3CouleurInfos, list_M10Vent) ->
                        val quantity = list_M10Vent.sumOf { it.quantity }
                        if (m3CouleurInfos != null) {
                            Parent_Dispo_Vent_View(
                                quantity = "Quantité: $quantity",
                                relative_M3CouleurInfos_KeyId = m3CouleurInfos.keyID,
                                relative_M1Produit_Nom = repositorysMainGetter
                                    .find_M1Produit(m3CouleurInfos.parentBProduitInfosKeyID)?.nom.empty_If_Null(
                                        ""
                                    )
                            )
                        }
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
