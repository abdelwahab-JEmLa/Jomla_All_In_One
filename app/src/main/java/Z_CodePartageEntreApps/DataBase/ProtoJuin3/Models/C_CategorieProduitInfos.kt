package Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models

import A.AtelierMobile.Test.ID1.Test.Shared.DataBase.Fonctions.Main.getKeyFireBase
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Entity
data class C_CategorieProduitInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var nom: String = "",
    var position: Int = 0,
    var displayedHeader: Boolean = false,
    val itsHeldPourDeplacement: Boolean = false,

    // Section Etates Mutable
    var dernierFireBaseUpdateTimestamps: Long = 0,

    // Section keyFireBase
    var keyFireBase: String = "",
) {
    fun withProperKeyFireBaseAndTimeTamp(): C_CategorieProduitInfos {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(id, nom) }
        return this.copy(
            keyFireBase = safeKey,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    companion object {
        val caRef =
            Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/C_CategorieProduitInfos")

        fun securedRemoveFireBaseDBC_CategorieProduitInfos() {
            caRef.removeValue()
        }
    }

}
