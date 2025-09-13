package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.f

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFormatterUtils
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.f.z.Com.ElevatedCardHeader
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Produit_Vent(
    produitKeyId: String,
    ventList: List<M10OperationVentCouleur>,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    modifier: Modifier = Modifier
) {

    val produit = remember(produitKeyId) {
        repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
    }

    val hasNonTrouve = remember(ventList) {
        ventList.any { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
    }

    val allNonTrouve = remember(ventList) {
        ventList.isNotEmpty() && ventList.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
    }

    // Format first vent creation time
    val firstVentCreationTime = remember(ventList) {
        ventList.firstOrNull()?.let { firstVent ->
            val timestamp = if (firstVent.creationTimestamps > 0) {
                firstVent.creationTimestamps
            } else {
                firstVent.dernierTimeTampsSynchronisationAvecFireBase
            }

            if (timestamp > 0) {
                val sdf = SimpleDateFormat("HH:mm:ss a", Locale.getDefault())
                sdf.format(Date(timestamp))
            } else {
                null
            }
        }
    }

    // Initialize PdfFormatterUtils
    val pdfFormatterUtils = remember { PdfFormatterUtils(repositorysMainGetter) }

    // TODO(1) FIXED: Product name display issue resolved
    // The issue was likely due to null produit or empty nom field
    // Added null safety checks and fallback display values
    produit?.let { nonNullProduit ->
        val displayName = if (nonNullProduit.nom.isNullOrBlank()) {
            "Produit #${nonNullProduit.keyID.take(8)}" // Fallback name using keyID
        } else {
            nonNullProduit.nom
        }

        Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (hasNonTrouve) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // ElevatedCardHeader now includes the up icon functionality
                ElevatedCardHeader(
                    productName = displayName, // Using displayName with fallback
                    produit = nonNullProduit,
                    hasNonTrouve = hasNonTrouve,
                    allNonTrouve = allNonTrouve,
                    ventList = ventList,
                    aCentralFacade = aCentralFacade
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Debug info to help identify the issue (remove in production)
                Text(
                    text = nonNullProduit.nom,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Show first vent creation time
                firstVentCreationTime?.let { timeString ->
                    Text(
                        text = "Première commande: $timeString",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(
                            alpha = 0.8f
                        )
                        else MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Show total quantity using PdfFormatterUtils.formatQuantity
                val totalQuantity = ventList.sumOf { it.quantity }
                val cartonSize = nonNullProduit.quantite_Boit_Par_Carton ?: 1
                val formattedQuantity = pdfFormatterUtils.formatQuantity(
                    qty = totalQuantity,
                    cartonSize = cartonSize,
                    produit = nonNullProduit
                )
                Text(
                    text = "Qyt: $formattedQuantity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Show number of operations
                Text(
                    text = "${ventList.size} couleur(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

            }
        }
    } ?: run {
        // Handle case where produit is null
        Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = "Produit non trouvé (ID: ${produitKeyId.take(8)}...)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ce produit n'existe plus dans la base de données",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
