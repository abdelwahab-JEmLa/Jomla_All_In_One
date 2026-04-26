package Application5.App.Repository

import EntreApps.Shared.Models.Compts
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.util.Calendar

@Entity
data class M19Etudiant(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var nom: String = "",
    var prenom: String = "",

    var parent_ousstad_key: String = Compts.AbdelwahabTravailleChezGros_KeyId.keyId,

    var num_telephone_parent: String = "",
    var age: Int = 7,
    var positon_don_classe: Int = 1,

    // REMOVED: nmbr_absence_sans_justification - now calculated from observations
    var imprime_justification : Boolean = false,
    var exclue_de_l_affiche_au_classe : Boolean = false,

    var question_par_non : String = "هل يعاني ابنكم من مرض معين جزاكم الله خيرا؟",

    var dernier_Soura_Wassale_Laha: SOUAR = SOUAR.El_Nasse,
    var dernier_Soura_sater: Int = 1,

    var dernier_takyim_dabte: Takiyim = Takiyim.Jayid,
    var tikrare: Int = 1,

    var tikrare_3arde: Int = 1,

    var mokarrare_hifde: SOUAR = SOUAR.El_Nasse,
    var mokarrare_hifde_sater: Int = 1,

    var moulahada_3ala_soulouk: MoulahadaSoulouk = MoulahadaSoulouk.Rien,
    var moulahada_makouba: String = "",

    var istedrak_kadim_Moukarare: SOUAR = SOUAR.El_Nasse,
    var istedrak_kadim_Akher_Soura_Wassale_Laha: SOUAR = SOUAR.El_Nasse,
    var istedrak_kadim_Takyim_hali: Takiyim = Takiyim.Maqboul,

    var absent: Boolean = false,
    var mokarrare_hifde_mahssou_li_3idat_souer: Int = 1,

    var creationTimestamps: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
) {
    enum class Takiyim(val arabicName: String) {
        Moumtaz("ممتاز"),
        Jayid_Jiddan("جيد جداً"),
        Fawk_Jayid("فوق الجيد"),
        Jayid("جيد"),
        Fawk_Makbol("فوق المقبول"),
        Maqboul("مقبول"),
        Lam_Yahfed("لم يحفظ")
    }

    enum class MoulahadaSoulouk(val arabicName: String) {
        Rien("---"),
        Moumtaz("ممتاز"),
        Jayid_Jiddan("جيد جداً"),
        Jayid("جيد"),
        Maqboul("مقبول"),
        Daeef("ضعيف")
    }

    /**
     * Calculate unjustified absences for the current month from observations
     * Counts only Raeeb observations without justification (tabrire_riyab is blank)
     */
    fun calculateUnjustifiedAbsences(
        observations: List<M20ObsarvationEtudion>,
        forMonth: Calendar? = null  // Add this parameter
    ): Int {
        val calendar = forMonth?.clone() as? Calendar ?: Calendar.getInstance()
        val targetMonth = calendar.get(Calendar.MONTH)
        val targetYear = calendar.get(Calendar.YEAR)


        return observations.count { obs ->
            val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
            obs.etudiant_keyID == keyID &&
                    obs.type == M20ObsarvationEtudion.Type.Raeeb &&
                    obs.tabrire_riyab.isBlank() && // Not justified
                    obsDate.get(Calendar.MONTH) == targetMonth &&
                    obsDate.get(Calendar.YEAR) == targetYear
        }
    }

    /**
     * Calculate total absences (justified + unjustified) for the current month
     */
    fun calculateTotalAbsences(
        observations: List<M20ObsarvationEtudion>
    ): Int {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return observations.count { obs ->
            val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
            obs.etudiant_keyID == keyID &&
                    obs.type == M20ObsarvationEtudion.Type.Raeeb &&
                    obsDate.get(Calendar.MONTH) == currentMonth &&
                    obsDate.get(Calendar.YEAR) == currentYear
        }
    }

    fun get_DebugInfos(): String {
        return buildString {
            append("Nom: $nom, ")
            append("Age: $age, ")
            append("Dernier Soura: ${dernier_Soura_Wassale_Laha.arabicName} (${dernier_Soura_sater}), ")
            append("Mokarrare: ${mokarrare_hifde.arabicName} (${mokarrare_hifde_sater})")
        }
    }

    companion object {
        const val keyModel = "ID19"

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases"
        ).child("Datas19Etudiant")

        fun generePushKey() = RepositorysMainSetter.Companion.genereUnPushKeyFireBase(ref)

        fun get_default2(): M19Etudiant {
            return M19Etudiant()
        }

        fun get_default(): M19Etudiant {
            return M19Etudiant()
        }
    }
}
