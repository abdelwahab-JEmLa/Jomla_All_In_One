package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List.View

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.runtime.Composable

@Composable
fun Parent_Dispo_Vent_StateFull(
    repositorysMainGetter: RepositorysMainGetter,
    relative_M10Vent: M10OperationVentCouleur
) {
    val relative_linkedParent_M10Vent =
        repositorysMainGetter.find_M10OperationVentCouleur(relative_M10Vent.linked_To_M10OperationVent_KeyID)
    val relative_M3Couleur_KeyId =
        relative_linkedParent_M10Vent?.parent_M3CouleurProduit_KeyID
    val client = repositorysMainGetter.find_M2Client_By_M10Vent(
        relative_M10Vent
    )

    Parent_Dispo_Vent_StateLess(
        client_nom = client?.nom ?: "Client inconnu",
        quantity = "Quantité: ${relative_M10Vent.quantity}",
        relative_M3Couleur_KeyId = relative_M3Couleur_KeyId
    )
}
