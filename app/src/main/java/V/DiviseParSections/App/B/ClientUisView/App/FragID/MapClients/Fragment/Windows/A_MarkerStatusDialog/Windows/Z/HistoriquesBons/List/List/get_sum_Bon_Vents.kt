package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M8BonVent

fun get_sum_Bon_Vents(
    repositorysMainGetter: RepositorysMainGetter,
    relative_M8BonVent: M8BonVent,
): Double {
    val its_Confirmation_de_TransactionKeyId =
        find_its_Confirmation_de_Transaction(repositorysMainGetter, relative_M8BonVent)?.keyID

    val relative_List_Vents =
        repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter {
            it.parent_M8BonVent_KeyId == its_Confirmation_de_TransactionKeyId
                    && it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
        }

    val sum_Bon_Vents = relative_List_Vents.sumOf {
        val parentM13TarificationPrix =
            repositorysMainGetter.find_M13Tarification_By_KeyID(it.parentM13TarificationKeyID)?.prixCurrency
                ?: 0.0

        it.quantity * parentM13TarificationPrix
    }

    return sum_Bon_Vents
}

fun find_its_Confirmation_de_Transaction(
    repositorysMainGetter: RepositorysMainGetter,
    relative_M8BonVent: M8BonVent
) = repositorysMainGetter.repo8BonVent.datasValue
    .sortedByDescending { it.creationTimestamps }
    .firstOrNull {
        it.parent_M2Client_KeyID == relative_M8BonVent.parent_M2Client_KeyID
                && it.creationTimestamps < relative_M8BonVent.creationTimestamps
                && it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
    }
