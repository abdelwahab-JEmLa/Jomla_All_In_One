package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur

fun updateListRelativeVentCouleurPrixVent(
    repo10OperationVentCouleur: Repo10OperationVentCouleur,
    filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali: List<M10OperationVentCouleur>,
    produitKey: String?,
    newPrix: Double
) {
    val ventCouleursDuProduitKey =
        filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali
            .filter { it.parentM1ProduitInfosKeyId == produitKey }

    ventCouleursDuProduitKey.forEach { vent ->
        repo10OperationVentCouleur.addOrUpdateData(
            vent.copy(
                provisoireMonPrix = newPrix
            )
        )
    }
}

fun ventCouleursDuProduitKey(
    produitKey: String,
    filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali: List<M10OperationVentCouleur>
) =
    filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali
        .filter { it.parentM1ProduitInfosKeyId == produitKey }

fun toggleEtateDeliveryNonTrouveVentOu(
    filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali: List<M10OperationVentCouleur>,
    repo10OperationVentCouleur: Repo10OperationVentCouleur,
    produitKey: String
) {
    ventCouleursDuProduitKey(
        filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali = filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali,
        produitKey = produitKey
    ).forEach { vent ->
        val newState =
            if (vent.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve)
                M10OperationVentCouleur.EtateDelivery.NonTrouve
            else M10OperationVentCouleur.EtateDelivery.Trouve

        repo10OperationVentCouleur.addOrUpdateData(vent.copy(etateDelivery = newState))
    }
}
