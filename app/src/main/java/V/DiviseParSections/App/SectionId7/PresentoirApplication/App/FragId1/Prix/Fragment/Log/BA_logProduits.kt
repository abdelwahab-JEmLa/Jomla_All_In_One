package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.OutputNoSqlModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Passive.strDateEtTempFromVidTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel

fun logProduits(value: OutputNoSqlModel, viewModel: TarificationViewModel) {
        value.produits.forEachIndexed { produitIndex, produit ->
            val isLastProduit = produitIndex == value.produits.size - 1
            val produitPrefix = V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log.TreePrefix.Type1.get(isLastProduit)

            val (produitDate, produitTime) = strDateEtTempFromVidTimestamp(produit.vidTimestamp)
            val relatedInfos = viewModel.getSqlProduitInfos(produit.id)

            val produitInfos = StringBuilder().apply {
                append(produitPrefix)
                append(" Product : ")
                append(produit.id)
                append("=(${relatedInfos?.nom ?: " Unknown "})")
            }.toString()

            println("$produitInfos, Date: $produitDate Time: $produitTime (${produit.clients.size} clients)")

            logClients(viewModel,produit.clients, isLastProduit)
        }
    }
