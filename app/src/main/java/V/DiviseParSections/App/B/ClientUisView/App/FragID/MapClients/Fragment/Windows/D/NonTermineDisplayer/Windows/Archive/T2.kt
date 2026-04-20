package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Archive

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun Main(
    modifier: Modifier = Modifier,
) {

}

@Composable
fun MainFilter(modifier: Modifier = Modifier, produitList: List<M8BonVent>) {
    val produitListFiltered by remember {
        derivedStateOf {
            produitList.filter { it.etateActuellementEst == M8BonVent.EtateActuellementEst.Cible }
        }
    }

    MainList(modifier = modifier, produitList = produitListFiltered)
}

@Composable
fun MainList(modifier: Modifier = Modifier, produitList: List<M8BonVent>) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(produitList) { item ->
            MainItem(bonAchate = item)
        }
    }
}

@Composable
fun MainItem(modifier: Modifier = Modifier, bonAchate: M8BonVent) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Client: ${bonAchate.parent_M2Client_DebugInfos}")
            Text(text = "Heure: ${bonAchate.heurDebutInString}")
            Text(text = "État: ${bonAchate.etateActuellementEst.nomArabe}")
            Text(text = "ID: ${bonAchate.vid}")
        }
    }
}

fun testData(): List<M8BonVent> {
    return listOf(
        M8BonVent(
            creationTimestamps = 1749010953513L,
            parent_M2Client_OldLongID = 15L,
            parent_M2Client_DebugInfos = "3omar_yousef",
            
            heurDebutInString = "05:22",
            etateActuellementEst = M8BonVent.EtateActuellementEst.Cible,
            vid = 10L,
        ),
        M8BonVent(
            creationTimestamps = 1748029628555L,
            parent_M2Client_OldLongID = 4L,
            parent_M2Client_DebugInfos = "abdelhamid",
            
            heurDebutInString = "20:47",
            heurFinInString = "Non Defini",
            etateActuellementEst = M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME,
            vocaleKeyID = "",
           // tagCeBonEstOuvertPourComptsIds = "false",
            sonVocaleEstEcoute = false,
            sonEcoutementEstFaitAutimestamps = 0L,
            cActive = false,
            vid = 5L,
        ),
        M8BonVent(
            creationTimestamps = 1748027276129L,
            parent_M2Client_OldLongID = 4L,
            parent_M2Client_DebugInfos = "abdelhamid",
            
            heurDebutInString = "20:07",
            etateActuellementEst = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
            vid = 4L,
            parent_M2Client_KeyID = RepositorysMainSetter.getListDesParentKeys("null")[M8BonVent.keyModel] ?: "",
            parentID8C2TypeTransactionKeyByParent = RepositorysMainSetter.getListDesParentKeys("null")[M8BonVent.EtateActuellementEst.keyModel] ?: ""
        ),
        M8BonVent(
            creationTimestamps = 1748029628742L,
            parent_M2Client_OldLongID = 4L,
            parent_M2Client_DebugInfos = "abdelhamid",
            
            heurDebutInString = "20:47",
            etateActuellementEst = M8BonVent.EtateActuellementEst.AVEC_MARCHANDISE,
            vid = 6L,
        )
    )
}
