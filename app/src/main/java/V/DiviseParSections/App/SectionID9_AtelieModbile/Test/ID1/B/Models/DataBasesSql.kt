package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.B_ClientInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.C_TypeTarificationInfos

data class DataBasesSql(
    val d_TarificationInfos: MutableList<D_TarificationInfos> = mutableListOf(),
    val refFireBaseD_TarificationInfos: String="D_TarificationInfos",



    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val refFireBaseA_ProduitInfos: String="A_ProduitInfos",

    val b_ClientInfosList: MutableList<B_ClientInfos> = mutableListOf(),
    val refFireBaseB_ClientInfos: String="B_ClientInfos",

    val c_TypeTarificationInfos: MutableList<C_TypeTarificationInfos> = mutableListOf(),
    val refFireBaseC_TypeTarificationInfos: String="C_TypeTarificationInfos",

    )


