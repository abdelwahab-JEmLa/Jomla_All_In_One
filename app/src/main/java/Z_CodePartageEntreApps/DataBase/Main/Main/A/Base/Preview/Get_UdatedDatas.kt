package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import android.util.Log
import androidx.compose.runtime.Composable
import org.koin.compose.koinInject

@Composable
fun get_UdatedDatas(
    aCentralFacade: ACentralFacade = koinInject(),
    oldDatas: List<OldDataBase_M1>,
): Unit {
    val currentProducts = aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue

    oldDatas.forEach { old ->
        val m1Produit_IN_New = currentProducts.find { it.id == old.id }

        if (m1Produit_IN_New != null) {
            val updatedProduct = m1Produit_IN_New.copy(
                quantite_Boit_Par_Carton = old.nmbrCaron
            )
        } else {
            Log.w(
                "getData_AvecUpdated_Carton",
                "Product with ID ${old.id} not found in current products"
            )
        }
    }
}
