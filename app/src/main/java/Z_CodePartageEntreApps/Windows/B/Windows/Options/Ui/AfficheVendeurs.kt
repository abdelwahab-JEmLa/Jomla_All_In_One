package Z_CodePartageEntreApps.Windows.B.Windows.Options.Ui

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import Z_CodePartageEntreApps.Windows.B.Windows.Options.A_OptionsControlsButtons_Main
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject


@Composable
fun MainScreen(
    repository: _0_0_HeadOfRepositorys_Repository = koinInject(),
    modifier: Modifier = Modifier,
) {

    val vendeurRepository = repository.repositorys_Model.repository_1_5_Vendeur
    val _1_5_VendeurList = vendeurRepository.modelDatasSnapList

    val _1_4_PeriodeVent_Repository = repository.repositorys_Model.repository_1_4_PeriodeVent
    val _1_4_PeriodeVentList = _1_4_PeriodeVent_Repository.modelDatasSnapList

    var ceTelephoneActiveComptID by remember { mutableStateOf(0L) }
    val active_1_4_PeriodeVentList by remember {
        mutableStateOf(
            _1_4_PeriodeVentList.last().vid
        )
    }

    if (false) {
        vendeurRepository.addDataAndReturneItVID(_1_5_Vendeur(nom = "W"))
        vendeurRepository.addDataAndReturneItVID(_1_5_Vendeur(nom = "M"))
        _1_4_PeriodeVent_Repository.addDataAndReturneItVID(_1_4_PeriodeVent(heurDebutInString = "1:mm"))
        _1_4_PeriodeVent_Repository.addDataAndReturneItVID(_1_4_PeriodeVent(heurDebutInString = "2:mm"))

    }

    ElevatedCard(modifier.background(Color.Red)) {

        Text("$ceTelephoneActiveComptID")

        HorizontalDivider(
            Modifier.height(50.dp)
        )

        Text("_1_5_Vendeur")

        LazyColumn(Modifier.fillMaxWidth()) {
            items(_1_5_VendeurList) { compt ->
                HorizontalDivider(
                    Modifier.height(20.dp)
                )
                Column {
                    if (compt.vid == ceTelephoneActiveComptID) {
                        Text(
                            "actPeriodeVent ", color = Color.Red
                        )
                    }


                    val vid = compt.vid
                    Text(
                        "vid>$vid", fontSize = 30.sp, modifier = Modifier.clickable {
                            ceTelephoneActiveComptID = compt.vid
                        })

                    val nom = compt.nom
                    Text(
                        "nom>${nom} ", fontSize = 30.sp
                    )


                }
            }

            item {
                HorizontalDivider(
                    Modifier.height(50.dp), color = Color.Red
                )

                Text("MainScreen_1_4_PeriodeVent_Repository")

            }

            items(_1_4_PeriodeVentList) { period ->
                HorizontalDivider(
                    Modifier.height(20.dp), color = Color.Red
                )
                Column {
                    if (active_1_4_PeriodeVentList == period.vid) {
                        Text(
                            "actPeriodeVent ", color = Color.Red
                        )
                    }

                    val vid = period.vid
                    Text(
                        "vid>$vid", fontSize = 30.sp
                    )


                    val nom = period.vendeur_ParentVID
                    Text(
                        "nom>$nom",
                        fontSize = 30.sp,
                    )

                    Text(
                        "heurDeb>${period.heurDebutInString} ", fontSize = 30.sp
                    )
                }
            }

        }
    }

}


@Preview
@Composable
private fun AffichePersonsPV(
) {
    Column {
        MainScreen()
    }
    A_OptionsControlsButtons_Main()
}
