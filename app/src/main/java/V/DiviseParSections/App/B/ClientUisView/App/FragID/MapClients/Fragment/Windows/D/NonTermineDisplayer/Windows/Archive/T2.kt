package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Archive

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.BSetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
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
fun MainFilter(modifier: Modifier = Modifier, produitList: List<GBonVent>) {
    val produitListFiltered by remember {
        derivedStateOf {
            produitList.filter { it.etateActuellementEst == GBonVent.EtateActuellementEst.Cible }
        }
    }

    MainList(modifier = modifier, produitList = produitListFiltered)
}

@Composable
fun MainList(modifier: Modifier = Modifier, produitList: List<GBonVent>) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(produitList) { item ->
            MainItem(bonAchate = item)
        }
    }
}

@Composable
fun MainItem(modifier: Modifier = Modifier, bonAchate: GBonVent) {
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

fun testData(): List<GBonVent> {
    return listOf(
        GBonVent(
            creationTimestamps = 1749010953513L,
            parentHClientOldID = 15L,
            nomClientConcerned = "3omar_yousef",
            parentPeriodeVentOldID = 7L,
            heurDebutInString = "05:22",
            etateActuellementEst = GBonVent.EtateActuellementEst.Cible,
            vid = 10L,
            parentID2ClientKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.keyModel] ?: "",
            parentID7VentPeriodeKeyByParent = BSetter.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7] ?: "",
            parentID8C2TypeTransactionKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel] ?: ""
        ),
        GBonVent(
            creationTimestamps = 1748029628555L,
            parentHClientOldID = 4L,
            nomClientConcerned = "abdelhamid",
            parentPeriodeVentOldID = 7L,
            heurDebutInString = "20:47",
            heurFinInString = "Non Defini",
            etateActuellementEst = GBonVent.EtateActuellementEst.A_COMMANDE_CONFIRME,
            vocaleKeyID = "",
           // tagCeBonEstOuvertPourComptsIds = "false",
            sonVocaleEstEcoute = false,
            sonEcoutementEstFaitAutimestamps = 0L,
            cActive = false,
            vid = 5L,
            parentID2ClientKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.keyModel] ?: "",
            parentID7VentPeriodeKeyByParent = BSetter.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7] ?: "",
            parentID8C2TypeTransactionKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel] ?: ""
        ),
        GBonVent(
            creationTimestamps = 1748027276129L,
            parentHClientOldID = 4L,
            nomClientConcerned = "abdelhamid",
            parentPeriodeVentOldID = 7L,
            heurDebutInString = "20:07",
            etateActuellementEst = GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
            vid = 4L,
            parentID2ClientKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.keyModel] ?: "",
            parentID7VentPeriodeKeyByParent = BSetter.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7] ?: "",
            parentID8C2TypeTransactionKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel] ?: ""
        ),
        GBonVent(
            creationTimestamps = 1748029628742L,
            parentHClientOldID = 4L,
            nomClientConcerned = "abdelhamid",
            parentPeriodeVentOldID = 7L,
            heurDebutInString = "20:47",
            etateActuellementEst = GBonVent.EtateActuellementEst.AVEC_MARCHANDISE,
            vid = 6L,
            parentID2ClientKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.keyModel] ?: "",
            parentID7VentPeriodeKeyByParent = BSetter.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7] ?: "",
            parentID8C2TypeTransactionKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel] ?: ""
        )
    )
}
