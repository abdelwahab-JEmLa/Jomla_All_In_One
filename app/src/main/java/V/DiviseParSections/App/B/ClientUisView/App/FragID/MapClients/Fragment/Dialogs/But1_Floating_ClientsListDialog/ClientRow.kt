package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_Floating_ClientsListDialog

import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
 fun ClientRow(
    client: M2Client,
    currentMode: ActiveCentralValues.Click_On_Marque,
    lastTransaction: M8BonVent?,
    getter: RepositorysMainGetter,
    onClick: () -> Unit,
    onCenterOnMap: () -> Unit,
    onUpdateSecteur: () -> Unit,
) {
    val sumBonVents = lastTransaction?.let { lastTransaction.montant_principale_du_type }

    // montant_principale_du_type = sumCredits - sumVersements (voir
    // M8BonVent.fun_calculative_du_main_val). Pour un client normal, positif =
    // il nous doit de l'argent, négatif = on lui doit (versements en trop).
    // Pour un fournisseur c'est l'inverse — voir M2Client.calculateCreditsMap.
    // On affiche donc toujours le montant réel (+ ou -) avec un libellé qui
    // en précise le sens, plutôt que de cacher les valeurs négatives.
    val creditLabel = if (lastTransaction != null && sumBonVents != null && sumBonVents != 0.0) {
        val dateHandler = DatesHandler()
        val date = dateHandler.getDateAndTimString(lastTransaction.creationTimestamps).date
        val doitNousDeArgent = if (client.its_Fournisseur_Grossisst_A_Jomla) sumBonVents < 0.0 else sumBonVents > 0.0
        val prefix = if (doitNousDeArgent) "+" else "-"
        "$prefix${"%.2f".format(abs(sumBonVents))} DA · $date"
    } else {
        null
    }

    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.Companion
                .size(10.dp)
                .background(color = currentMode.couleur, shape = CircleShape),
        )
        Column(modifier = Modifier.Companion.weight(1f)) {
            Text(
                text = client.nom,
                fontWeight = FontWeight.Companion.Medium,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (client.numTelephone.isNotEmpty() && client.numTelephone != "null") {
                Text(
                    text = client.numTelephone,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Companion.Gray,
                )
            }
            if (client.secteur.isNotBlank()) {
                Text(
                    text = "Secteur : ${client.secteur}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Companion.Gray,
                )
            }
            if (creditLabel != null) {
                Text(
                    text = "Crédit : $creditLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        // Ouvre le dialogue de modification du secteur pour CE client précis
        // (distinct du dialogue "Modifier le secteur" du FAB, qui met à jour
        // tous les clients ciblés d'un coup) — action indépendante de onClick.
        IconButton(onClick = onUpdateSecteur) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Modifier le secteur de ${client.nom}",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        // Centre la carte sur ce client sans déclencher l'action du mode actif
        // (Standard / Appeler / Navigation / ...) ni fermer le dialogue —
        // action indépendante de onClick.
        IconButton(onClick = onCenterOnMap) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Centrer la carte sur ${client.nom}",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
