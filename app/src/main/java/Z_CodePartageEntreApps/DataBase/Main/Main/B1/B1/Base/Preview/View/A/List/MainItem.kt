package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainItem(data: B1CouleurOuGoutProduitDataBase) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID: ${data.key}")
            Text("Product: ${data.parentBProduitNom}")
            Text("Color: ${data.nomCouleurStrSiSonImageDispo}")
            Text("Type: ${data.aAffiche}")
            Text("Image: ${data.nomImageFichie}")
            data.parentBProduitOldID?.let { Text("Parent ID: $it") }
        }
    }
}

