package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
data class _1_4_PeriodeVent(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section InfosDeBase
    var vendeur_ParentVID: Long = 0L,
    var startDateInString: String = getMainValeKey(),
    var heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    var endDateInString: String = "",

    // Section StatuesMutable
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.ENTRE_MAIS_PAS_CONFIRME,

    ) {
    enum class EtateActuellementEst {
        ENTRE_MAIS_PAS_CONFIRME,
        CONFIRME,
        NA_PAS_COMMANDE,
    }

    fun mainKeyVal(): String {
        return startDateInString
    }

    companion object {
        private fun getMainValeKey(): String {
            return SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())
        }
    }
}
