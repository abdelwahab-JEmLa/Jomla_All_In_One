package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.D_AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mongodb.kbson.BsonObjectId
import java.io.File

// Fixed: Changed return type to return both List<CouleurInfos> and the matching achat operation
data class CouleurInfosWithAchat(
    val couleurInfosList: List<CouleurInfos>,
    val matchingAchat: D_AchatOperation?
)

fun createCouleurInfosFromProduct(
    produit: ArticlesBasesStatsTable,
    achats: List<D_AchatOperation>
): CouleurInfosWithAchat {
    val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val couleurInfosList = mutableListOf<CouleurInfos>()
    var firstMatchingAchat: D_AchatOperation? = null

    listOf(
        produit.couleur1 to 1,
        produit.couleur2 to 2,
        produit.couleur3 to 3,
        produit.couleur4 to 4
    ).forEach { (couleur, index) ->
        if (!couleur.isNullOrBlank()) {
            val fileName = "${produit.id}_$index"
            val imageFile = listOf("jpg", "webp", "jpeg", "png")
                .map { File("$basePath/$fileName.$it") }
                .firstOrNull { it.exists() && it.canRead() && it.length() > 0 }
                ?: File("$basePath/NonTrouve.webp")

            // Find the matching achat operation for this color and get its quantity
            val matchingAchat = achats.find { achat ->
                achat.nomImageFichieOuApellationDuCouleur == "${produit.id}_$index" ||
                        achat.nomImageFichieOuApellationDuCouleur == couleur
            }

            // Store the first matching achat for the info component
            if (firstMatchingAchat == null && matchingAchat != null) {
                firstMatchingAchat = matchingAchat
            }

            val quantityAvailable = matchingAchat?.quantityAchete ?: 0

            couleurInfosList.add(
                CouleurInfos(
                    bsonObjectId = BsonObjectId(),
                    imageNameSiDispo = imageFile.name,
                    aAffiche = if (imageFile.exists()) CouleurInfos.Affiche.Image else CouleurInfos.Affiche.Nom,
                    imageCouleurFichie = imageFile,
                    nomSiDispo = couleur,
                    countDeDisponibility = quantityAvailable
                )
            )
        }
    }

    return CouleurInfosWithAchat(couleurInfosList, firstMatchingAchat)
}

@Composable
fun Infos(
    achat: D_AchatOperation?,
    modifier: Modifier = Modifier
) {
    achat?.let { achatData ->
        Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = achatData.nomImageFichieOuApellationDuCouleur,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Qty: ${achatData.quantityAchete}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${achatData.provisoireMonPrix}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

data class CouleurInfos(
    val bsonObjectId: BsonObjectId,
    val imageNameSiDispo: String = "NonTrouve.webp",
    val aAffiche: Affiche = Affiche.Image,
    val imageCouleurFichie: File,
    val nomSiDispo: String = "Non Defini Car Il Y Image",
    val countDeDisponibility: Int = 0
) {
    enum class Affiche {
        Image,
        Nom
    }
}
