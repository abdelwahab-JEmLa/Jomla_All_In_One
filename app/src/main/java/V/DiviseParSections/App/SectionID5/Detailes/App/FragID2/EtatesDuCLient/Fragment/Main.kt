package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@Composable
fun AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    _01_VentsHistoriquesDataBase_Repository: _01_VentsHistoriquesDataBase_Repository = koinInject(),
    modifier: Modifier = Modifier,
    idClient:Long,
) {
    val historiquesTransactionsDeCetteJourParIdClient =
        _01_VentsHistoriquesDataBase_Repository.modelDatasSnapList
            .find {  }
     LazyColumn {

     }
}
