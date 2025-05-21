package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.TypeTarificationEnumT2
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.Function.getStrDateTime
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class D_TarificationInfos(
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
