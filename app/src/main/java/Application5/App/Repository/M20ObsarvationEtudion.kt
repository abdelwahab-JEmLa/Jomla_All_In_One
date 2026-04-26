package Application5.App.Repository

import EntreApps.Shared.Models.Compts
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Entity
data class M20ObsarvationEtudion(
    @PrimaryKey
    var keyID: String = generePushKey(),

    var type: Type = Type.Tama_Hifdoha,
    var tabrire_riyab: String = "",

    var etudiant_keyID: String = "",

    var min_soura: SOUAR = SOUAR.El_Nasse,
    var min_aya: Int = 1,
    var min_sattre: Int = 1,

    var ila_soura: SOUAR = SOUAR.El_Nasse,
    var ila_aya: Int = 1,
    var ila_sattre: Int = 1,

    var tikrar: Int = 1,
    var el3arde: Int = 1,

    var takyim: M19Etudiant.Takiyim = M19Etudiant.Takiyim.Jayid,

    // Store custom moulahadat as comma-separated string
    var moulahadat_takyim_li_islahiha: String = "",

    var parent_ousstad_key: String = Compts.AbdelwahabTravailleChezGros_KeyId.keyId,

    var creationTimestamps: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    // AJOUT: Timestamp de la date de session pour filtrer par date
    var sessionDateTimestamp: Long = System.currentTimeMillis(),
) {
    enum class Type {
        Raeeb,
        Tama_Hifdoha,
        Moukarrar_Itmamouhou,
        Ousstad_kama_Bil_moundat,
    }

    // Helper methods for working with moulahadat as strings
    fun getMoulahadatList(): List<String> {
        if (moulahadat_takyim_li_islahiha.isBlank()) return emptyList()
        return moulahadat_takyim_li_islahiha
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun setMoulahadatList(moulahadatList: List<String>) {
        moulahadat_takyim_li_islahiha = moulahadatList
            .filter { it.isNotBlank() }
            .joinToString(",")
    }

    fun hasMoulahada(moulahada: String): Boolean {
        return getMoulahadatList().contains(moulahada)
    }

    fun addMoulahada(moulahada: String) {
        if (moulahada.isBlank()) return
        val currentList = getMoulahadatList().toMutableList()
        if (!currentList.contains(moulahada)) {
            currentList.add(moulahada)
            setMoulahadatList(currentList)
        }
    }

    fun removeMoulahada(moulahada: String) {
        val currentList = getMoulahadatList().toMutableList()
        currentList.remove(moulahada)
        setMoulahadatList(currentList)
    }

    companion object {
        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases"
        ).child("DatasM20ObsarvationEtudion")

        fun generePushKey() = RepositorysMainSetter.Companion.genereUnPushKeyFireBase(ref)

        fun get_default(): M20ObsarvationEtudion {
            return M20ObsarvationEtudion()
        }
    }
}
