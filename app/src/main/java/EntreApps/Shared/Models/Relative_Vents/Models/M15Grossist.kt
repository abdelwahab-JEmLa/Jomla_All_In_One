package EntreApps.Shared.Models.Relative_Vents.Models

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import androidx.room.Entity
import androidx.room.PrimaryKey


enum class Fournisseur_Speciale(
    val keyID: String = "",
    val autre_nom: String = "",
    var latitude: Double = 36.720027701275505,
    var longitude: Double = 3.1436710147865483,
    val moulahada: String = "",
) {
    Ami_Jamel(),
}
@Entity
data class M15Grossist(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    var nom: String = "Non Definie",
    var couleur_In_Str: String = "#229DAD",

    //---------------------------------ForgingKeys.----------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------

) {
    fun get_DebugInfos(): String {
        return buildString {
            append("(M15=")
            append(nom)
            append("[")
            append(keyID.takeLast(3).uppercase())
            append("])")
        }
    }

    companion object {
        val ref = RepositorysMainGetter.Companion.centralRef.child("DatasM15Grossist")

        fun generePushKey() = ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")

        fun safeRemoveRef(): Unit {
            ref.removeValue()
        }

        fun get_default(
        ): M15Grossist {
            val data = M15Grossist()
            return data
        }

        fun find_By_MainValues_Depuit_List(
            data_A_Cherche_Par_MainValues: M15Grossist,
            data_List: List<M15Grossist>,
        ) = data_List
            .find { data ->
                data.nom == data_A_Cherche_Par_MainValues.nom
            }
    }
}
