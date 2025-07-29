package P0_MainScreen.Main

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.CouleurDisplayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun App_PresenterEcran_Au_Client(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedActiveValuesFacade: FocusedActiveValuesFacade = aCentralFacade.focusedActiveValuesFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    modifier: Modifier = Modifier
) {
    val focusedValuesGetter = focusedActiveValuesFacade.focusedValuesGetter
    val active_ProduitKeyID_Au_DroopDown_PresenterEcran =
        focusedValuesGetter.currentActive_M9AppCompt?.active_ProduitKeyID_Au_DroopDown_PresenterEcran

    val relative_List_Couleurs = active_ProduitKeyID_Au_DroopDown_PresenterEcran?.let {
        repositorysMainGetter.find_ListM3CouleurInfos_By_Parent_Produit_KeyID(
            it
        )
    }

    var clickedCouleurKeyID by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(relative_List_Couleurs.orEmpty()) { couleur ->
                val isClicked = clickedCouleurKeyID == couleur.keyID
                val itemHeight = if (isClicked) 500.dp else 100.dp

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    onClick = {
                        clickedCouleurKeyID = if (isClicked) null else couleur.keyID
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Display the color using CouleurDisplayer
                        CouleurDisplayer(
                            keyCouleur = couleur.keyID,
                            size = if (isClicked) 80.dp else 40.dp,
                            modifier = Modifier.size(if (isClicked) 80.dp else 40.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = couleur.nomImageFichieSansEtansion,
                                style = if (isClicked) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
                            )

                            if (couleur.nomCouleurStrSiSonImageDispo.isNotBlank()) {
                                Text(
                                    text = couleur.nomCouleurStrSiSonImageDispo,
                                    style = if (isClicked) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Show additional details when clicked
                            if (isClicked) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Extension: ${couleur.extensionDisponible}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Index: ${couleur.indexCouleurDansAncienProto}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
