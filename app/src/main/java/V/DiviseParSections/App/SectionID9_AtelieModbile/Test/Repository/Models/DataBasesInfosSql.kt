package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.D1_Tariff
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.B_ClientInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.C_TypeTarificationInfos

data class DataBasesInfosSql(
    val d_TarificationInfos: MutableList<D1_Tariff> = mutableListOf(),
    val refFireBaseD_1Tariff: String="D1_Tariff",


    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val refFireBaseA_ProduitInfos: String="A_ProduitInfos",

    val b_ClientInfosList: MutableList<B_ClientInfos> = mutableListOf(),
    val refFireBaseB_ClientInfos: String="B_ClientInfos",

    val c_TypeTarificationInfos: MutableList<C_TypeTarificationInfos> = mutableListOf(),
    val refFireBaseC_TypeTarificationInfos: String="C_TypeTarificationInfos",

    )
