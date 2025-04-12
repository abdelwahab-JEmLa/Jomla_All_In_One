package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun A_MainScreen_APP2_FragID3(
    modifier: Modifier = Modifier,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject(),
) {
    val models = _0_0_HeadOfRepositorys_Repository.repositorys_Model
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        LazyColumn {
            items(
                models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                    .filter {
                        it.etateActuellementEst ==
                                _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                    }
                    .distinctBy { it.produitAcheterID }
            )
            { Produit ->
                HorizontalDivider(Modifier.padding(10.dp), thickness = 2.dp)
                ProduitCommande(models, Produit)
            }
        }
    }
}

