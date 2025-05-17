package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.A.Test.Models.createTimestamp
import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

data class DataBasesInfosSql(
    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val refFireBaseA_ProduitInfos: String="A_ProduitInfos",

    val b_ClientInfos: MutableList<B_ClientInfos> = mutableListOf(),
    val refFireBaseB_ClientInfos: String="B_ClientInfos",

    val c_TypeTarificationInfos: MutableList<C_TypeTarificationInfos> = mutableListOf(),
    val refFireBaseC_TypeTarificationInfos: String="C_TypeTarificationInfos",

    val d_TarificationInfos: MutableList<D_TarificationInfos> = mutableListOf(),
    val refFireBaseD_TarificationInfos: String="D_TarificationInfos",

    )

// Updated entity classes in DataBasesInfosSql.kt
@Entity
data class A_ProduitInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "",
    val keyFireBase: String = getkeyFireBase(id, nom),

    val needUpdate: Boolean = false
)

@Entity
data class B_ClientInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "Non Difinie",
    val keyFireBase: String = getkeyFireBase(id, nom),

    val idActiveTypeTarificationDataBase: Long = 0,
    val needUpdate: Boolean = false
)

@Entity
data class C_TypeTarificationInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entityCorrespond: TypeTarificationEnum = TypeTarificationEnum.ParBenifice,
    val nom: String= entityCorrespond.name,
    val keyFireBase: String = getkeyFireBase(id, nom),

    val needUpdate: Boolean = false
)

@Entity
data class D_TarificationInfos(
    @PrimaryKey
    val vidTimestamp: Long = 0L,
    val nom: String= getStrDateTime(),
    val keyFireBase: String = getkeyFireBase(dataNom=nom),
    val idProduit: Long = 0L,
    val idClient: Long = 0L,
    val idTypeTarification: Long = 0L,
    val prixCurrency: Double = 0.0,

    val needUpdate: Boolean = false
)

fun getkeyFireBase(
    dataId: Long? = null,
    dataNom: String? = null
): String {
     return if (dataId != null) {
         "-<$dataId($dataNom)"
     } else {
         "-<$dataNom"
     }
}

enum class TypeTarificationEnum {
    ParBenifice,
    Historique,
    LeMaxPrixArrive
}

fun testDatasDataBasesInfosSql(): DataBasesInfosSql {
    return DataBasesInfosSql(
        a_ProduitInfos = mutableListOf(
            A_ProduitInfos(id = 1, nom = "Produit Optila"),
            A_ProduitInfos(id = 2, nom = "Produit Hnina"),
            A_ProduitInfos(id = 3, nom = "Produit kemya")
        ),
        b_ClientInfos = mutableListOf(
            B_ClientInfos(
                id = 1,
                nom = "ClientAchteur Abderrahman",
                idActiveTypeTarificationDataBase = 1
            ),
            B_ClientInfos(
                id = 2,
                nom = "ClientAchteur Beta",
                idActiveTypeTarificationDataBase = 2
            ),
            B_ClientInfos(
                id = 3,
                nom = "ClientAchteur Gamma",
                idActiveTypeTarificationDataBase = 3
            )
        ),
        c_TypeTarificationInfos = mutableListOf(
            C_TypeTarificationInfos(
                id = 1,
                entityCorrespond = TypeTarificationEnum.ParBenifice,
                nom = "Par Bénifice",
                keyFireBase = getkeyFireBase(1, "Par Bénifice")
            ),
            C_TypeTarificationInfos(
                id = 2,
                entityCorrespond = TypeTarificationEnum.Historique,
                nom = "Historique",
                keyFireBase = getkeyFireBase(2, "Historique")
            ),
            C_TypeTarificationInfos(
                id = 3,
                entityCorrespond = TypeTarificationEnum.LeMaxPrixArrive,
                nom = "Prix Maximum",
                keyFireBase = getkeyFireBase(3, "Prix Maximum")
            )
        ),
        d_TarificationInfos = mutableListOf(
            D_TarificationInfos(
                vidTimestamp = createTimestamp(day = 1, hour = 12, minute = 30),
                idProduit = 1,
                idClient = 1,
                idTypeTarification = 1,
                prixCurrency = 20.99
            ),
            D_TarificationInfos(
                vidTimestamp = createTimestamp(day = 5, hour = 13, minute = 30),
                idProduit = 1,
                idClient = 1,
                idTypeTarification = 1,
                prixCurrency = 25.50
            ),
            D_TarificationInfos(
                vidTimestamp = createTimestamp(day = 5, hour = 14, minute = 30),
                idProduit = 1,
                idClient = 2,
                idTypeTarification = 2,
                prixCurrency = 9.75
            ),
            D_TarificationInfos(
                vidTimestamp = createTimestamp(day = 6, hour = 3, minute = 30),
                idProduit = 2,
                idClient = 1,
                idTypeTarification = 1,
                prixCurrency = 15.25
            ),
            D_TarificationInfos(
                vidTimestamp = createTimestamp(day = 6, hour = 4, minute = 30),
                idProduit = 3,
                idClient = 1,
                idTypeTarification = 3,
                prixCurrency = 14.80
            )
        )
    )
}

fun createTimestamp(year: Int = 2025, month: Int = 5, day: Int, hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day, hour, minute, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

@SuppressLint("DefaultLocale")
fun getStrDateTime(): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    // Format: yyyy-mm-dd -< HH:mm:ss
    return String.format("%04d-%02d-%02d -< %02d:%02d:%02d", year, month, day, hour, minute, second)
}
