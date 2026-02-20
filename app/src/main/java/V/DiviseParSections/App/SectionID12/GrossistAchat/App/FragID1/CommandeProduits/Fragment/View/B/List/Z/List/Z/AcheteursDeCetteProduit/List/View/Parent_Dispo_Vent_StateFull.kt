package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List.View

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import androidx.compose.runtime.Composable
import org.koin.compose.koinInject

@Composable
fun Parent_Dispo_Vent_StateFull(
    relative_M10Vent: M10OperationVentCouleur,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    relative_M2Client: M2Client?,
    relative_M1Produit: M01Produit?
) {
    val relative_linkedParent_M10Vent = repositorysMainGetter.find_M10OperationVentCouleur(relative_M10Vent.linked_To_M10OperationVent_KeyID)
    val relative_M3Couleur_KeyId = relative_linkedParent_M10Vent?.parent_M3CouleurProduit_KeyID

    Parent_Dispo_Vent_View(
        quantity = "Quantité: ${relative_M10Vent.quantity}",
        relative_M3CouleurInfos_KeyId = relative_M3Couleur_KeyId,
        relative_M1Produit_Nom = relative_M1Produit?.nom ?:""
    )
}
