package Z_CodePartageEntreApps.Proto.Par.Type.Models

import Z_CodePartageEntreApps.Model.getKeyFireBase
import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
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
    val id: Long = 0L,

    //Forging IDs
    val idParentProduit: Long = 0L,
    val typeTarificationEnumT2Correspond: TypeTarificationEnumT2 =
        TypeTarificationEnumT2.PRIX_BASE,

    val parentIdClient: Long = 0L,

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
        val safeKey = keyFireBase.ifEmpty {
            getKeyFireBase(id, properNom)
        }
        return this.copy(
            nom = properNom,
            keyFireBase = safeKey,
            needUpdate = true
        )
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

enum class TypeTarificationEnumT2(
    val iconVector: ImageVector? = null,
    val couleur: Color = Color.White,
    val nomArabe: String ="",
) {
    LeMaxPrixArrive(Icons.Filled.ArrowUpward, Color(0xFFFF9800),"فائدة محققة مع لاضا كثير من الزيناء"),
    DEFINI(Icons.Filled.Edit, Color(0xFFFFEB3B),"المحدد من المدير بنصرف "),
    Historique(Icons.Filled.History, Color(0xFF2196F3),"السعر الذي وصلنا له"),
    PRIX_BASE(Icons.Filled.EditOff, Color(0xFFF44336),"الفايدة ابتداءا تكاد تكون معدومة ")
}
