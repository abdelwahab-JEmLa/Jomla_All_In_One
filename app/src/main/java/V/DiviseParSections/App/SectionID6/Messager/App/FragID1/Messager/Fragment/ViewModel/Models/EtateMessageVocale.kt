package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models

import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity
data class EtateMessageVocale(
    @PrimaryKey(autoGenerate = true)
    val vid: Long=0,

    //Forging Keys
    val parentMessageVID: Long = 0,
    val parentMessageKeyID: String =
        "SecteurDeClients.vid(SecteurDeClients.nom)",

    //Infos De Base
    var nom: Nom = Nom.EN_COURT_ENREGESTREMENT,
    var timestamps: Long = DatesHandler().getCurrentTimestamps(),

    //Etates Mutable

) {
    val fireBaseKeyID: String
        get() {
            val parent = "($parentMessageVID)->"
            val thisVal = "($vid)->(${nom}_($timestamps))"

            return "$parent$thisVal"
        }

    enum class Nom(val nomArabe: String? = null) {
        EN_COURT_ENREGESTREMENT,
        ENVOYER,
        VUE,
        ECOUTE,
    }

    // Test instance function with random value implementation
    companion object {
        fun createTestInstance(parentMessageVID: Long, parentMessageKeyID: String): EtateMessageVocale {
            // Generate a random number between 1 and 9
            val randomNumber = Random.nextInt(1, 10) // Generates 1-9

            return EtateMessageVocale(
                vid = System.currentTimeMillis() + randomNumber,
                parentMessageVID = parentMessageVID,
                parentMessageKeyID = parentMessageKeyID,
                nom = when (randomNumber % 3) {
                    0 -> Nom.EN_COURT_ENREGESTREMENT
                    1 -> Nom.VUE
                    else -> Nom.ECOUTE
                },
                timestamps = DatesHandler().getCurrentTimestamps()
            )
        }
    }
}
