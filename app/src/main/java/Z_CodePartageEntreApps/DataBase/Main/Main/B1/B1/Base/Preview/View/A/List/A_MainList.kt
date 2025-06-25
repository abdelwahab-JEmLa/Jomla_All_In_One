package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.B1CouleurOuGoutProduitDataBaseRepository
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
 fun MainList(
    b1CouleurOuGoutProduitDataBaseRepository: B1CouleurOuGoutProduitDataBaseRepository,
) {
    val items = b1CouleurOuGoutProduitDataBaseRepository.datasValue
      
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items) { data ->
            LazyRowProduit(data)
        }
    }
}

@Composable
private fun LazyRowProduit(data: B1CouleurOuGoutProduitDataBase) {
    //<--
    //TODO(1): fait groupe par idParent produit et 
    MainItem(data)
}
