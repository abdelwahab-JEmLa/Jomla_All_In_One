package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.Function.getStrDateTime
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

@Entity
data class D1_Tariff(
    @PrimaryKey
    val id: Long = 0L,
    //Forging IDs
    val idParentProduit: Long = 0L,
    val typeTarificationEnumT2Correspond: TypeTarificationEnumT2 = TypeTarificationEnumT2.PRIX_BASE,
    val idParentBonAchat: Long = 0L,

    //Base Infos
    val prixCurrency: Double = 0.0,
    val timestamps: Long = 0,
    val nom: String = getStrDateTime(id),

    //keyFireBase
    val keyFireBase: String = getKeyFireBase(dataNom = nom),

    //Etates Mutable
    val needUpdate: Boolean = true
)

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
