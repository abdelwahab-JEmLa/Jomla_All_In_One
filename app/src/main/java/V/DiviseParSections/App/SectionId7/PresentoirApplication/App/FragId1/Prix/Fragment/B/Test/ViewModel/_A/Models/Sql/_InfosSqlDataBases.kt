package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql

import androidx.room.Entity
import androidx.room.PrimaryKey
data class _InfosSqlDataBases(
    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val b_ClientInfos: MutableList<B_ClientInfos> = mutableListOf(),
    val c_TypeTarificationInfos: MutableList<C_TypeTarificationInfos> = mutableListOf(),
    val d_TarificationInfos: MutableList<D_TarificationInfos> = mutableListOf()
)



@Entity(tableName = "produits")
data class A_ProduitInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = ""
)

@Entity(tableName = "clients")
data class B_ClientInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "Non Difinie",
    val idActiveTypeTarificationDataBase: Long = 0,
)

@Entity(tableName = "type_tarifications")
data class C_TypeTarificationInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val typeTarificationEnum: TypeTarificationEnum = TypeTarificationEnum.ParBenifice
)

@Entity(tableName = "tarifications")
data class D_TarificationInfos(
    @PrimaryKey
    val vidTimestamp: Long = 0L,
    val idProduit: Long = 0L,
    val idClient: Long = 0L,
    val idTypeTarification: Long = 0L,
    val prixCurrency: Double = 0.0
)
