package V.DiviseParSections.App.C_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models

import V.DiviseParSections.App.C_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.Function.getStrDateTime
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
    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val refFireBaseA_ProduitInfos: String="A_ProduitInfos",

    val b_ClientInfosList: MutableList<B_ClientInfos> = mutableListOf(),
    val refFireBaseB_ClientInfos: String="B_ClientInfos",

    val c_TypeTarificationInfos: MutableList<C_TypeTarificationInfos> = mutableListOf(),
    val refFireBaseC_TypeTarificationInfos: String="C_TypeTarificationInfos",

    val d_TarificationInfos: MutableList<D_TarificationInfos> = mutableListOf(),
    val refFireBaseD_TarificationInfos: String="D_TarificationInfos",

    )

@Entity
data class A_ProduitInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "",
    val keyFireBase: String = getKeyFireBase(id, nom),

    val needUpdate: Boolean = true
)

@Entity
data class B_ClientInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "Non Difinie",
    val keyFireBase: String = getKeyFireBase(id, nom),

    var cLeDataOuvertDuParentList: Boolean = false,

    val needUpdate: Boolean = true
)

@Entity
data class C_TypeTarificationInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entityCorrespond: TypeTarificationEnum = TypeTarificationEnum.ParBenifice,
    val nom: String= entityCorrespond.name,
    val keyFireBase: String = getKeyFireBase(id, nom),

    val needUpdate: Boolean = true
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

enum class TypeTarificationEnum(val icon: ImageVector? = null, val couleur: Color = Color.White) {
    ParBenifice(Icons.Filled.ShoppingCart, Color(0xFF4CAF50)),
    Historique(Icons.Filled.History, Color(0xFF2196F3)),
    LeMaxPrixArrive(Icons.Filled.ArrowUpward, Color(0xFFFF9800)),
    PRIX_BASE(Icons.Filled.AttachMoney, Color(0xFFF44336))
}
