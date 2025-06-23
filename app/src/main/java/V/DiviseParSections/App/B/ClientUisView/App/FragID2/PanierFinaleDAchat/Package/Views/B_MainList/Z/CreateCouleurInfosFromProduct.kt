package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.Module.LazyRowAvailableColorsImageOuNom
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.BProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.D_AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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


@Composable
fun Images(
    productId: String?,
    achats: List<D_AchatOperation>,
    bProduitDataBaseComposeRepositoryPJ17: BProduitDataBaseComposeRepositoryPJ17
) {
    val relatedProduitDataBase = bProduitDataBaseComposeRepositoryPJ17
        .datasValue
        .find { it.bsonObjectId == productId }

    createCouleurInfosFromProduct(
        relatedProduitDataBase,
        achats
    ).let {
        if (it.couleurInfosList.isNotEmpty()) {
            LazyRowAvailableColorsImageOuNom(
                couleurInfos = it.couleurInfosList,
                couleurInfosWithAchat_matchingAchat = it.matchingAchat,
                sizeDeChaqueItem = 100.dp
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

data class CouleurInfosWithAchat(
    val couleurInfosList: List<CouleurInfos>,
    val matchingAchat: D_AchatOperation?
)

data class CouleurInfos(
    val bsonObjectId: BsonObjectId,
    val imageNameSiDispo: String = "NonTrouve.webp",
    val aAffiche: Affiche = Affiche.Image,
    val imageCouleurFichie: File,
    val nomSiDispo: String = "Non Defini Car Il Y Image",
    val countDeDisponibility: Int = 0,
    val colorIndex: Int = -1,
    val imageExists: Boolean = false
) {
    enum class Affiche { Image, Nom }
}

fun createCouleurInfosFromProduct(
    produit: ArticlesBasesStatsTable?,
    achats: List<D_AchatOperation>
): CouleurInfosWithAchat {
    val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val couleurInfosList = mutableListOf<CouleurInfos>()
    var firstMatchingAchat: D_AchatOperation? = null

    val colorMappings = listOf(
        produit?.couleur1 to 0,
        produit?.couleur2 to 1,
        produit?.couleur3 to 2,
        produit?.couleur4 to 3
    )

    colorMappings.forEach { (couleur, colorIndex) ->
        if (!couleur.isNullOrBlank()) {
            val imageIndex = colorIndex + 1
            val fileName = "${produit?.id}_$imageIndex"

            val imageFile = listOf("jpg", "webp", "jpeg", "png")
                .map { File("$basePath/$fileName.$it") }
                .firstOrNull { it.exists() && it.canRead() && it.length() > 0 }
                ?: File("$basePath/NonTrouve.webp")

            val imageExists = imageFile.name != "NonTrouve.webp" &&
                    imageFile.exists() && imageFile.canRead() && imageFile.length() > 0

            val matchingAchat = achats.find { achat ->
                achat.nomImageFichieOuApellationDuCouleur == fileName ||
                        achat.nomImageFichieOuApellationDuCouleur == couleur
            }

            if (firstMatchingAchat == null && matchingAchat != null) {
                firstMatchingAchat = matchingAchat
            }

            couleurInfosList.add(
                CouleurInfos(
                    bsonObjectId = BsonObjectId(),
                    imageNameSiDispo = imageFile.name,
                    aAffiche = if (imageExists) CouleurInfos.Affiche.Image else CouleurInfos.Affiche.Nom,
                    imageCouleurFichie = imageFile,
                    nomSiDispo = couleur,
                    countDeDisponibility = matchingAchat?.quantityAchete ?: 0,
                    colorIndex = colorIndex,
                    imageExists = imageExists
                )
            )
        }
    }

    return CouleurInfosWithAchat(couleurInfosList, firstMatchingAchat)
}


@Composable
fun Infos(achat: D_AchatOperation?, modifier: Modifier = Modifier) {
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
