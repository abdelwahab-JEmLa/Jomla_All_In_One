package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.Module

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.D_AchatOperation
import org.mongodb.kbson.BsonObjectId
import java.io.File


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

