package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main

import A.AtelierMobile.Test.ID1.Test.Shared.DataBase.Fonctions.Main.getKeyFireBase
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Entity
data class D_EtateMessageVocale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    //Forging Keys
    val parentMessageVID: Long = 0,

    //Infos De Base
    var nom: Nom = Nom.EN_COURT_ENREGESTREMENT,
    var timestamps: Long = DatesHandler().getCurrentTimestamps(),

    //Etates Mutable

    // Section keyFireBase et dernierFireBaseUpdateTimestamps
    var keyFireBase: String = "",
    var dernierFireBaseUpdateTimestamps: Long = 0,
    ) {
    enum class Nom(val nomArabe: String? = null) {
        EN_COURT_ENREGESTREMENT,
        ENVOYER,
        VUE,
        ECOUTE,
    }

    fun withProperKeyFireBaseAndTimeTamp(): D_EtateMessageVocale {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(id, nom.name) }
        return this.copy(
            keyFireBase = safeKey,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    companion object {
        fun createTestInstance(): List<D_EtateMessageVocale> {
            val baseTime = System.currentTimeMillis()

            return listOf(
                D_EtateMessageVocale(
                    id = baseTime + 1,
                    parentMessageVID = 1L,
                    nom = Nom.EN_COURT_ENREGESTREMENT,
                    timestamps = DatesHandler().getCurrentTimestamps()
                ),
                D_EtateMessageVocale(
                    id = baseTime + 2,
                    parentMessageVID = 2L,
                    nom = Nom.VUE,
                    timestamps = DatesHandler().getCurrentTimestamps()
                ),
                D_EtateMessageVocale(
                    id = baseTime + 3,
                    parentMessageVID = 3L,
                    nom = Nom.ECOUTE,
                    timestamps = DatesHandler().getCurrentTimestamps()
                )
            )
        }

        val parent = Firebase.database.getReference("00_DataPrototype-04-02" +
                "/_1_developingRef" +
                "/C_InfosSqlDataBases" )

        val caRef = parent.child("D_EtateMessageVocale")
    }
}
