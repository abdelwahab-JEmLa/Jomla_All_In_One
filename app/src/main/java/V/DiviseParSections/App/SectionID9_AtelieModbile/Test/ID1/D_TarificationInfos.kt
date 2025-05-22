package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class D_TarificationInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L, // 0L ensures auto-increment works properly

    //Forging IDs
    val idParentProduit: Long = 0L,
    val typeTarificationEnumT2Correspond: TypeTarificationEnumT2 =
        TypeTarificationEnumT2.PRIX_BASE,

    val idParentBonAchat: Long = 0L,

    //Base Infos
    val prixCurrency: Double = 0.0,
    val timestamps: Long = System.currentTimeMillis(),
    val nom: String = "",

    //Etates Mutable
    val needUpdate: Boolean = true,

    //keyFireBase - computed property, not stored in constructor
    val keyFireBase: String = "",
) {
    fun withProperDefaults(): D_TarificationInfos {
        val properNom = nom.ifEmpty { getStrDateTime(timestamps) }
        return this.copy(
            nom = properNom,
            keyFireBase = getKeyFireBase(id, properNom)
        )
    }

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
        calendar.timeInMillis = vidTimestamp

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Format: yyyy-mm-dd -< HH:mm:ss
        return String.format("%04d-%02d-%02d -< %02d:%02d:%02d", year, month, day, hour, minute, second)
    }

}
fun getKeyFireBase(
    dataId: Long? = null,
    dataNom: String? = null
): String {
    return if (dataId != null) {
        "-<$dataId($dataNom)"
    } else {
        "-<$dataNom"
    }
}

enum class TypeTarificationEnumT2(
    val iconVector: ImageVector? = null,
    val couleur: Color = Color.White,
    val nomArabe: String ="",
) {
    LeMaxPrixArrive(Icons.Filled.ArrowUpward, Color(0xFFFF9800),"فائدة محققة مع لاضا كثير من الزيناء"),
    DEFINI(Icons.Filled.Edit, Color(0xFFFFEB3B),"المحدد من المدير بنصرف "),
    AU_GERANT(Icons.Filled.Done, Color(0xFF4CAF50),"التقدير للمدير "),
    Historique(Icons.Filled.History, Color(0xFF2196F3),"السعر الذي وصلنا له"),
    PRIX_BASE(Icons.Filled.EditOff, Color(0xFFF44336),"الفايدة ابتداءا تكاد تكون معدومة ")
}
