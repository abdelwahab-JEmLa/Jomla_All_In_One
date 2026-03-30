package Z_CodePartageEntreApps.Modules.D.Glide

import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M01Produit
import android.util.Log
import com.google.firebase.database.Exclude
import org.mongodb.kbson.BsonObjectId
import java.io.File


data class CouleurInfosWithAchat(
    val couleurInfosList: List<FileCouleurInfos>,
    val matchingAchat: M10OperationVentCouleur?
)
enum class Affiche {
    Image, Nom
}
// FIXED: FileCouleurInfos class to prevent circular references
data class FileCouleurInfos(
    @get:Exclude
    @Transient
    val d_CouleurVentOperation: M10OperationVentCouleur? = null, // FIXED: Made nullable

    val keyID: String = "",
    val bsonObjectId: BsonObjectId = BsonObjectId(),

    val aAffiche: Affiche = Affiche.Image,

    @get:Exclude
    @Transient
    val imageCouleurFichie: File? = null,

    val imageCouleurFichiePath: String = imageCouleurFichie?.absolutePath ?: "",

    val nomCouleurStrSiSonImageDispo: String = "",
    val quantityDeDisponibility: Int = 0,
    val colorIndex: Int = 0,
) {



    fun getImageFile(): File? {
        return try {
            if (imageCouleurFichiePath.isNotEmpty()) {
                File(imageCouleurFichiePath)
            } else {
                imageCouleurFichie
            }
        } catch (e: Exception) {
            Log.e("FileCouleurInfos", "Error creating File from path: $imageCouleurFichiePath", e)
            null
        }
    }

    // FIXED: Create Firebase-safe version - Remove @get:Exclude annotation
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "keyID" to keyID,
            "key" to bsonObjectId.toString(), // Use toString() instead of toHexString()
            "aAffiche" to aAffiche.name,
            "imageCouleurFichiePath" to imageCouleurFichiePath,
            "nomCouleurStrSiSonImageDispo" to nomCouleurStrSiSonImageDispo,
            "quantityDeDisponibility" to quantityDeDisponibility,
            "colorIndex" to colorIndex
        )
    }

    companion object {
        // FIXED: Factory method to create FileCouleurInfos safely
        fun createSafe(
            keyID: String = "",
            aAffiche: Affiche = Affiche.Image,
            imageCouleurFichie: File? = null,
            nomCouleurStrSiSonImageDispo: String = "",
            quantityDeDisponibility: Int = 0,
            colorIndex: Int = 0,
        ): FileCouleurInfos {
            return FileCouleurInfos(
                d_CouleurVentOperation = null, // Always null to prevent circular reference
                keyID = keyID,
                bsonObjectId = BsonObjectId(),
                aAffiche = aAffiche,
                imageCouleurFichie = imageCouleurFichie,
                imageCouleurFichiePath = imageCouleurFichie?.absolutePath ?: "",
                nomCouleurStrSiSonImageDispo = nomCouleurStrSiSonImageDispo,
                quantityDeDisponibility = quantityDeDisponibility,
                colorIndex = colorIndex
            )
        }
    }
}

fun createCouleurInfosFromProduct(
    produit: M01Produit?,
    achats: List<M10OperationVentCouleur>
): CouleurInfosWithAchat {
    val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val couleurInfosList = mutableListOf<FileCouleurInfos>()
    var firstMatchingAchat: M10OperationVentCouleur? = null

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


            couleurInfosList.add(
                FileCouleurInfos(
                    bsonObjectId = BsonObjectId(),
                    aAffiche = if (imageExists) Affiche.Image else Affiche.Nom,
                    imageCouleurFichie = imageFile,
                    nomCouleurStrSiSonImageDispo = couleur,
                    colorIndex = colorIndex,
                )
            )
        }
    }

    return CouleurInfosWithAchat(couleurInfosList, firstMatchingAchat)
}
