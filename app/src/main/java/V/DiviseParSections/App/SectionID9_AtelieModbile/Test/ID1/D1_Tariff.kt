package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

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

