package EntreApps.Shared.Models

import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.central_Local_storageLink
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.CollectionReference
import java.io.File

@Entity
data class
M3CouleurProduitInfos(
    @PrimaryKey
    var keyID: String = RepositorysMainGetter.Companion.getPushFireBase(ref),
    var debugInfos: String = "",
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    val processPositioningInFactory: ProcessPositioningInFactory = ProcessPositioningInFactory.CreeAuGeneralHandler,
    val aAffiche: Type = Type.Image,
    val dropBox_key: String = "Non Dispo",
    val nomImageFichieSansEtansion: String = "Non Dispo",

    val telephone_Prise_depuit: String = "",

    var count_Don_Depot: Int = 0,

    var a_cammende_depuit_grossist: Int = 0,

    val nomCouleurStrSiSonImageDispo: String = "",

    //-------------------------------Filters---------------------------------------------------------------------------------------------------------------------------------
    var its_pour_affiche_au_presenter: Boolean? = null,
    var its_in_echantiallants: Boolean? = null,

    //---------------------------------Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parentProduit_Classement: Int? = null,

    var parentBProduitInfosKeyID: String = "",

    var parentBProduitOldID: Long = 0,
    var parentId1ProduitInfosDebugName: String = "",
    var indexCouleurDansAncienProto: Int = 0,


    val extensionDisponible: String = "webp", // Default extension
) {

    fun toFirebaseMap(): Map<String, Any?> = mapOf(
        "keyID" to keyID,
        "debugInfos" to debugInfos,
        "creationTimestamp" to creationTimestamp,
        "dernierTimeTampsSynchronisationAvecFireBase" to dernierTimeTampsSynchronisationAvecFireBase,
        "its_in_echantiallants" to its_in_echantiallants,

        "its_pour_affiche_au_presenter" to its_pour_affiche_au_presenter,
        "parentProduit_Classement" to parentProduit_Classement,

        "processPositioningInFactory" to processPositioningInFactory.name,
        "aAffiche" to aAffiche.name,
        "nomImageFichieSansEtansion" to nomImageFichieSansEtansion,
        "telephone_Prise_depuit" to telephone_Prise_depuit,
        "count_Don_Depot" to count_Don_Depot,
        "a_cammende_depuit_grossist" to a_cammende_depuit_grossist,
        "nomCouleurStrSiSonImageDispo" to nomCouleurStrSiSonImageDispo,
        "parentBProduitInfosKeyID" to parentBProduitInfosKeyID,
        "parentBProduitOldID" to parentBProduitOldID,
        "parentId1ProduitInfosDebugName" to parentId1ProduitInfosDebugName,
        "indexCouleurDansAncienProto" to indexCouleurDansAncienProto,
        "extensionDisponible" to extensionDisponible,
    )

    fun get_DebugsInfos(): String {
        return buildString {
            append("03Coul")
            append("[")
            append("{${keyID.takeLast(4).uppercase()}}\n")
            append(" To ")
            append("[")
            append("{${parentId1ProduitInfosDebugName.takeLast(4).uppercase()}}\n")
            append("]")
            append("]")
        }
    }

    enum class Type { Nom, Image }
    enum class ProcessPositioningInFactory { CreeDepuitRechercheRapid, CreeAuGeneralHandler }

    companion object {
        val ref = M00CentralParametresOfAllApps.centralRef
            .child("B1CouleurOuGoutProduitDataBase")

        //Second Nom Ref_Active_Keys_M03Couleurs
        val ref_listKeys_M3CouleurProduitInfos = ref.child("-00_listKeys_M3CouleurProduitInfos")

        val refFirestore: CollectionReference = RepositorysMainGetter.firestoreCentralRefData
            .document("M3CouleurProduitInfos")
            .collection("Datas")

        val images_central_Local_storageLink = buildString {
            append(central_Local_storageLink)
            append("/IMGs/BaseDonne")
        }

        val baseDir = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne")

        val rootFolder_DropBox = buildString {
            append("/images")
        }

        fun compareEntre(
            ancien: M3CouleurProduitInfos,
            newData: M3CouleurProduitInfos
        ) =
            ancien.parentBProduitOldID == newData.parentBProduitOldID &&
                    ancien.nomCouleurStrSiSonImageDispo == newData.nomCouleurStrSiSonImageDispo &&
                    ancien.nomImageFichieSansEtansion == newData.nomImageFichieSansEtansion

        fun get_default(): M3CouleurProduitInfos {
            return M3CouleurProduitInfos()
        }

        fun decrementing_file_name_si_non_trouve(
            nomImageFichieSansEtansion: String,
            extensionDisponible: String
        ): String? {
            if (nomImageFichieSansEtansion == "Non Dispo") {
                return null
            }

            val images_central_Local_storageLink = buildString {
                append(central_Local_storageLink)
                append("/IMGs/BaseDonne")
            }
            // Try the original filename first
            var currentFile = File(baseDir, "$nomImageFichieSansEtansion.$extensionDisponible")
            if (currentFile.exists()) {
                return nomImageFichieSansEtansion
            }

            // If filename doesn't contain underscore, no decrementing possible
            if (!nomImageFichieSansEtansion.contains("_")) {
                return null
            }

            // Extract prefix and number
            val prefix = nomImageFichieSansEtansion.substringBeforeLast("_")
            val initialNumber =
                nomImageFichieSansEtansion.substringAfterLast("_").toIntOrNull() ?: return null

            // Try decrementing the number until we find an existing file or reach 0
            for (number in (initialNumber - 1) downTo 0) {
                val fileName = "${prefix}_${number}"
                currentFile = File(baseDir, "$fileName.$extensionDisponible")
                if (currentFile.exists()) {
                    return fileName
                }
            }

            // No valid file found
            return null
        }

        fun incrementing_file_name(nomImageFichieSansEtansion: String): String? {
            if (nomImageFichieSansEtansion == "Non Dispo") {
                return null
            }

            // If filename doesn't contain underscore, can't increment
            if (!nomImageFichieSansEtansion.contains("_")) {
                return null
            }

            // Extract prefix and number
            val prefix = nomImageFichieSansEtansion.substringBeforeLast("_")
            val currentNumber =
                nomImageFichieSansEtansion.substringAfterLast("_").toIntOrNull() ?: return null

            // Return incremented filename
            return "${prefix}_${currentNumber + 1}"
        }
    }
}

data class Ref_list_Filtred_Keys_M3Couleur_Main_Values(
    val nom: String = "",
    val classment: Int = 0,
    val activated: Boolean = false,
    val parentProduitKeyID: String = "",
    val parentProduitDebugName: String = "",
    val parentProduitClassement: Int = 0,
    val its_couleur_du_Jomla_ECHATILLANTS_Client: Client_Speciale? = null
)
