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
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros &&
                    tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
        }

    fun find_existing_Tariff_Grossist_Achat(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
    ) = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
        .lastOrNull { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Achat &&
                    tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
        }

    fun find_existing_Tariff_Grossist_Progressive(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
    ) = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
        .lastOrNull { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive &&
                    tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
        }

    fun find_existing_Tariff_Grossist_Gro(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
    ) = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
        .lastOrNull { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro &&
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
                typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Achat,
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
                typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
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
                typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive,
                prixCurrency = calculateProgressiveGrossistPrice(aCentralFacade, relative_M1Produit),
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
                typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro,
                prixCurrency = calculateGroPrice(relative_M1Produit),
                parent_M1Produit_KeyId = relative_M1Produit.keyID,
                parent_M1Produit_DebugInfos = relative_M1Produit.nom,
                creationTimestamps = System.currentTimeMillis()
            )
    }

    private fun calculateSuperGrosPrice(relative_M1Produit: ArticlesBasesStatsTable): Double {
        return relative_M1Produit.prixAchat
    }

    private fun calculateProgressiveGrossistPrice(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable
    ): Double {
        // Get the actual current prices from existing tariffs, not the calculated base prices
        val superGrosTariff = find_existing_Tariff_Grossist_SuperGros(aCentralFacade, relative_M1Produit)
        val groTariff = find_existing_Tariff_Grossist_Gro(aCentralFacade, relative_M1Produit)

        val superGrosPrice = superGrosTariff?.prixCurrency ?: calculateSuperGrosPrice(relative_M1Produit)
        val groPrice = groTariff?.prixCurrency ?: calculateGroPrice(relative_M1Produit)

        return (superGrosPrice + groPrice) / 2
    }

    /**
     * Updates the progressive tariff when SuperGros or Gro tariffs are modified
     */
    fun updateProgressiveTariffOnRelatedChange(
        aCentralFacade: ACentralFacade,
        relative_M1Produit: ArticlesBasesStatsTable,
        focusedValuesGetter: FocusedValuesGetter
    ) {
        val existingProgressiveTariff = find_existing_Tariff_Grossist_Progressive(aCentralFacade, relative_M1Produit)

        if (existingProgressiveTariff != null) {
            val newProgressivePrice = calculateProgressiveGrossistPrice(aCentralFacade, relative_M1Produit)

            // Only update if price has changed
            if (newProgressivePrice != existingProgressiveTariff.prixCurrency) {
                val updatedProgressiveTariff = existingProgressiveTariff.copy(
                    prixCurrency = newProgressivePrice,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )

                aCentralFacade.repositorysMainSetter.upsert_M13TarificationInfos(updatedProgressiveTariff)
            }
        }
    }

    private fun calculateGroPrice(relative_M1Produit: ArticlesBasesStatsTable): Double {
        // This should probably have different logic than SuperGros
        // For now, adding a small margin to differentiate from SuperGros
        return calculateSuperGrosPrice(relative_M1Produit) * 1.1 // 10% markup over SuperGros
    }
}
