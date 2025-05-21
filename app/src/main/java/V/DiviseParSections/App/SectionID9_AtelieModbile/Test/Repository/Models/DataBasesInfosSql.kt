package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.Function.getStrDateTime
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.B_ClientInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.C_TypeTarificationInfos
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

data class DataBasesInfosSql(

    val d_TarificationInfos: MutableList<D_TarificationInfos> = mutableListOf(),
    val refFireBaseD_TarificationInfos: String="D_TarificationInfos",


    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val refFireBaseA_ProduitInfos: String="A_ProduitInfos",

    val b_ClientInfosList: MutableList<B_ClientInfos> = mutableListOf(),
    val refFireBaseB_ClientInfos: String="B_ClientInfos",

    val c_TypeTarificationInfos: MutableList<C_TypeTarificationInfos> = mutableListOf(),
    val refFireBaseC_TypeTarificationInfos: String="C_TypeTarificationInfos",

    )

@Entity
data class D_TarificationInfos(
    @PrimaryKey
    val vidTimestamp: Long = 0L,
    val nom: String= getStrDateTime(vidTimestamp),
    val keyFireBase: String = getKeyFireBase(dataNom=nom),
    val idProduit: Long = 0L,
    val idClient: Long = 0L,
    val idTypeTarification: Long = 0L,
    val prixCurrency: Double = 0.0,

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

enum class TypeTarificationEnum(val iconVector: ImageVector? = null, val couleur: Color = Color.White) {
    ParBenifice(Icons.Filled.ShoppingCart, Color(0xFF4CAF50)),
    Historique(Icons.Filled.History, Color(0xFF2196F3)),
    LeMaxPrixArrive(Icons.Filled.ArrowUpward, Color(0xFFFF9800)),
    PRIX_BASE(Icons.Filled.AttachMoney, Color(0xFFF44336))
}
