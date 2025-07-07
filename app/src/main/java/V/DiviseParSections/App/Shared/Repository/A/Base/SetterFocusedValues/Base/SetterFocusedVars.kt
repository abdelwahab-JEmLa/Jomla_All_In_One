package V.DiviseParSections.App.Shared.Repository.A.Base.SetterFocusedValues.Base

import V.DiviseParSections.App.Shared.Repository.A.Base.GetterFocusedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.MainSetterFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.SetterFocusedValues.Base.Functions.ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt
import V.DiviseParSections.App.Shared.Repository.A.Base.SetterFocusedValues.Base.Functions.anulleFocucePourPrixDeM1Produit
import V.DiviseParSections.App.Shared.Repository.A.Base.SetterFocusedValues.Base.Functions.focuceOnVentM3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.SetterFocusedValues.Base.Functions.focucePourPrixDeM1Produit
import V.DiviseParSections.App.Shared.Repository.A.Base.SetterFocusedValues.Base.Functions.updateCurrentAppComptDialogProduit
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.runtime.Stable

@Stable
class SetterFocusedVars(
    val mainSetterFacade: MainSetterFacade,

    val getterFocusedVars: GetterFocusedVars,
    val Repo2Client: Repo2Client,
    val repo8BonVent: Repo8BonVent,
    val repo9AppCompt: Repo9AppCompt,
    val repo10OperationVentCouleur: Repo10OperationVentCouleur,
) {
    fun saveTariff_Et_RelateIt_Au_Vents_Correspond() {
        val focused_M13TarificationInfos_Pour_Produit =
            getterFocusedVars.focused_M13TarificationInfos_Pour_Produit

        focused_M13TarificationInfos_Pour_Produit?.let {
            mainSetterFacade.addOrUpdateGroAliTariff(it)

            val listFocusedM10OpeVentCouleurParPrixDifineur =
                getterFocusedVars.focused_ListM10OpeVentCouleur_Par_PD_M1Produit.map { listVent ->
                    listVent.copy(
                        parentM13TarificationDebugInfos = focused_M13TarificationInfos_Pour_Produit.getDebugInfos(),
                        parentM13TarificationKeyID = focused_M13TarificationInfos_Pour_Produit.keyID,
                        provisoireMonPrix = focused_M13TarificationInfos_Pour_Produit.prixCurrency
                    )
                }

            mainSetterFacade.updateListM10OperationVentCouleur(
                listFocusedM10OpeVentCouleurParPrixDifineur = listFocusedM10OpeVentCouleurParPrixDifineur
            )
        }
    }

    fun addNewM8BonVent(id8BonVent: M8BonVent) =
        ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(id8BonVent, repo8BonVent)

    fun addNewM2ClientInfos(newClient: HClientInfos) = Repo2Client.addClient(newClient)

    fun ajouteNewM10OperationVentCouleur(it: M10OperationVentCouleur) {
        repo10OperationVentCouleur.addOrUpdateData(it)
    }

    fun updateFocuceM9AppCompt(data: Z_AppCompt) = repo9AppCompt.upsert(data)

    fun ouvrireDialogChoisireQuantity(
        m10OperationVentCouleur: M10OperationVentCouleur
    ) = focuceOnVentM3CouleurProduitInfos(
        m10OperationVentCouleur = m10OperationVentCouleur,
        getterFocusedVars = getterFocusedVars,
        repo9AppCompt = repo9AppCompt,
    )

    fun fermeDialogChoisireQuantityDeVentCouleur(produitKey: String) {
        focuceOnVentM3CouleurProduitInfos(
            getterFocusedVars = getterFocusedVars,
            repo9AppCompt = repo9AppCompt,
        )
        focucePourPrixDeM1Produit(
            produitKey,
            getterFocusedVars,
            repo9AppCompt
        )
    }

    fun ouvrireM1ProduitDialogChoisireQuantityFacade(produit: ArticlesBasesStatsTable) =
        updateCurrentAppComptDialogProduit(
            getterFocusedVars,
            repo9AppCompt,
            produit,
        )

    fun focucePourPrixDeM1ProduitFacade(produit: ArticlesBasesStatsTable) {
        focucePourPrixDeM1Produit(
            produit.keyID,
            getterFocusedVars,
            repo9AppCompt
        )
    }

    fun fermeFocucePourPrixDeM1ProduitDialogChoisireQuantityFacade(produit: ArticlesBasesStatsTable) {
        updateCurrentAppComptDialogProduit(getterFocusedVars, repo9AppCompt)
        focucePourPrixDeM1Produit(
            produit.keyID,
            getterFocusedVars,
            repo9AppCompt
        )
    }

    fun anulleFocucePourPrixDeM1ProduitFacade() {
        anulleFocucePourPrixDeM1Produit(getterFocusedVars, repo9AppCompt)
    }

}
