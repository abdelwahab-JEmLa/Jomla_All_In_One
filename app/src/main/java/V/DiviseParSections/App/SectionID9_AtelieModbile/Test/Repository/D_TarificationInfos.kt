package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.TypeTarificationEnumT2
import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class D_TarificationInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    //Forging IDs
    val idParentProduit: Long = 0L,
    val typeTarificationEnumT2Correspond: TypeTarificationEnumT2 =
        TypeTarificationEnumT2.PRIX_BASE,

    val idParentBonAchat: Long = 0L,

    //Base Infos
    val prixCurrency: Double = 0.0,
    val timestamps: Long = 0,
    val nom: String = getStrDateTime(timestamps),

    //Etates Mutable
    val needUpdate: Boolean = true,

    //keyFireBase
    val keyFireBase: String = getKeyFireBase(id, nom),
)

fun getKeyFireBase(
    dataId: Long? = null,
    dataNom: String? = null
): String {
    return if (dataId != null && dataId != 0L) {
        "-<$dataId($dataNom)"
    } else {
        "-<$dataNom"
    }
}

@SuppressLint("DefaultLocale")
fun getStrDateTime(vidTimestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = vidTimestamp  // Use the provided timestamp

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    // Format: yyyy-mm-dd -< HH:mm:ss
    return String.format("%04d-%02d-%02d -< %02d:%02d:%02d", year, month, day, hour, minute, second)
}
