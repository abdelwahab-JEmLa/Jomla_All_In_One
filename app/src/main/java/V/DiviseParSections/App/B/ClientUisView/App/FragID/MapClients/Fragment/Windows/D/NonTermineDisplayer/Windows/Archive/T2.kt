package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Archive

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.GmodelTransactionCommercial
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun Main(
    modifier: Modifier = Modifier,
) {
    val produitListLocal by remember { mutableStateOf(testData()) }
    MainFilter(produitList = produitListLocal)
}

@Composable
fun MainFilter(modifier: Modifier = Modifier, produitList: List<GmodelTransactionCommercial>) {
    val produitListFiltered by remember {
        derivedStateOf {
            produitList.filter { it.etateActuellementEst == GmodelTransactionCommercial.EtateActuellementEst.Cible }
        }
    }

    MainList(modifier = modifier, produitList = produitListFiltered)
}

@Composable
fun MainList(modifier: Modifier = Modifier, produitList: List<GmodelTransactionCommercial>) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(produitList) { item ->
            MainItem(bonAchate = item)
        }
    }
}

@Composable
fun MainItem(modifier: Modifier = Modifier, bonAchate: GmodelTransactionCommercial) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Client: ${bonAchate.nomClientConcerned}")
            Text(text = "Heure: ${bonAchate.heurDebutInString}")
            Text(text = "État: ${bonAchate.etateActuellementEst.nomArabe}")
            Text(text = "ID: ${bonAchate.vid}")
        }
    }
}

fun testData(): List<GmodelTransactionCommercial> {
    return listOf(
        GmodelTransactionCommercial(
            vid = 10L,
            parentVID_1_4_PeriodeVent = 7L,
            clientAcheteurID = 15L,
            nomClientConcerned = "3omar_yousef",
            timestamps = 1749010953513L,
            heurDebutInString = "05:22",
            etateActuellementEst = GmodelTransactionCommercial.EtateActuellementEst.Cible
        ),
        GmodelTransactionCommercial(
            vid = 5L,
            parentVID_1_4_PeriodeVent = 7L,
            clientAcheteurID = 4L,
            nomClientConcerned = "abdelhamid",
            timestamps = 1748029628555L,
            heurDebutInString = "20:47",
            heurFinInString = "Non Defini",
            cActive = false,
           // tagCeBonEstOuvertPourComptsIds = "false",
            vocaleKeyID = "",
            sonVocaleEstEcoute = false,
            sonEcoutementEstFaitAutimestamps = 0L,
            etateActuellementEst = GmodelTransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME
        ),
        GmodelTransactionCommercial(
            vid = 4L,
            parentVID_1_4_PeriodeVent = 7L,
            clientAcheteurID = 4L,
            nomClientConcerned = "abdelhamid",
            timestamps = 1748027276129L,
            heurDebutInString = "20:07",
            etateActuellementEst = GmodelTransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        ),
        GmodelTransactionCommercial(
            vid = 6L,
            parentVID_1_4_PeriodeVent = 7L,
            clientAcheteurID = 4L,
            nomClientConcerned = "abdelhamid",
            timestamps = 1748029628742L,
            heurDebutInString = "20:47",
            etateActuellementEst = GmodelTransactionCommercial.EtateActuellementEst.AVEC_MARCHANDISE
        )
    )
}
