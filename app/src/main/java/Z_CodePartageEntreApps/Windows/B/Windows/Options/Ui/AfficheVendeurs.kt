package Z_CodePartageEntreApps.Windows.B.Windows.Options.Ui

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
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
    var ceTelephoneActiveComptID by remember { mutableStateOf(2L) }

    val vendeurRepository = repository.repositorys_Model
        .repository_1_5_Vendeur
    val _1_5_Vendeur = vendeurRepository.modelDatasSnapList

    ElevatedCard(modifier.background(Color.Red)) {

        Text("$ceTelephoneActiveComptID")

        HorizontalDivider(
            Modifier.height(50.dp)
        )

        Text("_1_5_Vendeur")

        LazyColumn(Modifier.fillMaxWidth()) {
            items(_1_5_Vendeur) {
                Column {
                    val vid = it.vid
                    Text(
                        "vid>$vid",
                        fontSize = 30.sp   ,
                        modifier=Modifier.clickable {
                            ceTelephoneActiveComptID = it.vid
                        }

                    )
                    HorizontalDivider(
                        Modifier.height(20.dp)
                    )
                    val nom = it.nom
                    Text(
                        "nom>$nom",
                        fontSize = 30.sp
                    )
                    HorizontalDivider(
                        Modifier.height(20.dp)
                    )
                    Text(
                        "idPeri>${it.idPeriodActivePourCeCompt}",
                        fontSize = 30.sp
                    )
                }
            }
        }
    }
    HorizontalDivider(
        Modifier.height(50.dp), color = Color.Red
    )

    val _1_4_PeriodeVent_Repository = repository.repositorys_Model
        .repository_1_4_PeriodeVent
    val _1_4_PeriodeVent = _1_4_PeriodeVent_Repository.modelDatasSnapList

    ElevatedCard(modifier.background(Color.Red)) {
        Text("MainScreen_1_4_PeriodeVent_Repository")
        LazyColumn(Modifier.fillMaxWidth()) {
            items(_1_4_PeriodeVent) { period ->
                Column {
                    val vid = period.vid
                    Text(
                        "vid>$vid",
                        fontSize = 30.sp
                    )

                    HorizontalDivider(
                        Modifier.height(10.dp), color = Color.Red
                    )

                    val nom = period.vendeur_ParentVID
                    Text(
                        "nom>$nom",
                        fontSize = 30.sp ,
                        modifier=Modifier.clickable {
                            // Find the vendor with the active account ID
                            val vendeur = _1_5_Vendeur.find { it.vid == ceTelephoneActiveComptID }

                            vendeur?.let { v ->
                                // Update the active period ID
                                v.idPeriodActivePourCeCompt = period.vid

                                // Save the update vendor
                                repository.upsertUneDataEtReturnVID(v)
                            }
                        }
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
