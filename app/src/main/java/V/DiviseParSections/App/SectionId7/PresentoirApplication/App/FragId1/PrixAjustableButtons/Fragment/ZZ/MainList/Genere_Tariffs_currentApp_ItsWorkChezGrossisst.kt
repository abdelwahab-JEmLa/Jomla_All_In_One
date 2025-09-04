package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos

class Genere_Tariffs_currentApp_ItsWorkChezGrossisst {

    fun find_existing_Tariff_Grossist_SuperGros(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
    ) = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
        .lastOrNull { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_Grossist_SuperGros &&
                    tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
        }

    fun find_existing_Tariff_Grossist_Achat(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
    ) = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
        .lastOrNull { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_Grossist_Achat &&
                    tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
        }

    fun find_existing_Tariff_Grossist_Progressive(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
    ) = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
        .lastOrNull { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_Grossist_Progressive &&
                    tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
        }

    fun find_existing_Tariff_Grossist_Gro(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
    ) = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
        .lastOrNull { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_Grossist_Gro &&
                    tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
        }

    fun getOrCreate_Tariff_Grossist_Achat(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
        focusedValuesGetter: FocusedValuesGetter
    ): M13TarificationInfos {
        return find_existing_Tariff_Grossist_Achat(aCentralFacade, relative_M1Produit)
            ?: M13TarificationInfos(
                parent_M14VentPeriod_KeyId = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
                    ?: "",
                typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_Grossist_Achat,
                prixCurrency = relative_M1Produit.prixAchat,
                parent_M1Produit_KeyId = relative_M1Produit.keyID,
                parent_M1Produit_DebugInfos = relative_M1Produit.nom,
                creationTimestamps = System.currentTimeMillis()
            )
    }

    fun getOrCreate_Tariff_Grossist_SuperGros(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
        focusedValuesGetter: FocusedValuesGetter
    ): M13TarificationInfos {
        return find_existing_Tariff_Grossist_SuperGros(aCentralFacade, relative_M1Produit)
            ?: M13TarificationInfos(
                parent_M14VentPeriod_KeyId = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
                    ?: "",
                typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_Grossist_SuperGros,
                prixCurrency = calculateSuperGrosPrice(relative_M1Produit),
                parent_M1Produit_KeyId = relative_M1Produit.keyID,
                parent_M1Produit_DebugInfos = relative_M1Produit.nom,
                creationTimestamps = System.currentTimeMillis()
            )
    }

    fun getOrCreate_Tariff_Grossist_Progressive(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
        focusedValuesGetter: FocusedValuesGetter
    ): M13TarificationInfos {
        return find_existing_Tariff_Grossist_Progressive(aCentralFacade, relative_M1Produit)
            ?: M13TarificationInfos(
                parent_M14VentPeriod_KeyId = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
                    ?: "",
                typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_Grossist_Progressive,
                prixCurrency = calculateProgressiveGrossistPrice(relative_M1Produit),
                parent_M1Produit_KeyId = relative_M1Produit.keyID,
                parent_M1Produit_DebugInfos = relative_M1Produit.nom,
                creationTimestamps = System.currentTimeMillis()
            )
    }

    fun getOrCreate_Tariff_Grossist_Gro(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
        focusedValuesGetter: FocusedValuesGetter
    ): M13TarificationInfos {
        return find_existing_Tariff_Grossist_Gro(aCentralFacade, relative_M1Produit)
            ?: M13TarificationInfos(
                parent_M14VentPeriod_KeyId = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
                    ?: "",
                typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_Grossist_Gro,
                prixCurrency = calculateGroPrice(relative_M1Produit),
                parent_M1Produit_KeyId = relative_M1Produit.keyID,
                parent_M1Produit_DebugInfos = relative_M1Produit.nom,
                creationTimestamps = System.currentTimeMillis()
            )
    }

    private fun calculateSuperGrosPrice(relative_M1Produit: ArticlesBasesStatsTable): Double {
        // Super Gros price: cost price + minimal margin (5-10%)
        val minimalMargin = 0.05 // 5% minimal margin
        return relative_M1Produit.prixAchat * (1 + minimalMargin)
    }

    private fun calculateProgressiveGrossistPrice(relative_M1Produit: ArticlesBasesStatsTable): Double {
        // Progressive price: between super gros and regular gros
        val superGrosPrice = calculateSuperGrosPrice(relative_M1Produit)
        val groPrice = calculateGroPrice(relative_M1Produit)
        return (superGrosPrice + groPrice) / 2
    }

    private fun calculateGroPrice(relative_M1Produit: ArticlesBasesStatsTable): Double {
        // Gro price: cost price + standard margin (15-20%)
        val standardMargin = 0.15 // 15% standard margin
        return relative_M1Produit.prixAchat * (1 + standardMargin)
    }

    private fun calculateProgressivePrice(
        prixDetaille: Double,
        prixVent: Double,
        pourcentageProgressive: Int
    ): Double {
        val priceDifference = prixDetaille - prixVent
        val pourcentageProgressive1 =
            if (pourcentageProgressive == 50) 60 else pourcentageProgressive
        val progressiveAdjustment = priceDifference * (pourcentageProgressive1 / 100.0)
        return prixVent + progressiveAdjustment
    }
}
