package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.functions_central.runtime_throw_Erreur_Pour_Regle_Le_Real_Bug
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ClientEdites(
    viewModel: MapClientsViewModel,
    focusedValuesSetter: FocusedValuesSetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter,
    repo2Client: Repo2Client = viewModel.aCentralFacade.repositorysMainGetter.repo2Client,
    relative_Client: M2Client?,
    onDismiss: () -> Unit,
    onClickToEditeMarquerPosition: (M2Client) -> Unit,
    onShowDeleteConfirmationChange: (Boolean) -> Unit = {},
    onClientTypeModeChange: (M2Client.ClientTypeMode?) -> Unit = {},
    onShowEditDialogChange: (Boolean) -> Unit = {},
    onShowPhoneDialogChange: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val clientTypeMode = relative_Client?.clientTypeMode
    val hasPhoneNumber = !relative_Client?.numTelephone.isNullOrEmpty() &&
            relative_Client?.numTelephone != "null"
    val hasValidLocation = relative_Client?.latitude != null &&
            relative_Client?.latitude != 0.0 &&
            relative_Client?.longitude != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Delete Icon
        Card(
            modifier = Modifier
                .background(color = Color.Red)
                .clickable {
                    onShowDeleteConfirmationChange(true)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete client",
                modifier = Modifier.padding(8.dp)
            )
        }

        // Phone Call Icon - only visible if phone number exists
        if (hasPhoneNumber) {
            Card(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        val phoneNumber = relative_Client?.numTelephone ?: ""
                        try {
                            // Try Truecaller first
                            val truecallerIntent = Intent(
                                Intent.ACTION_DIAL,
                                Uri.fromParts("tel", phoneNumber, null)
                            ).apply {
                                setPackage("com.truecaller")
                            }

                            val packageManager = context.packageManager
                            val isTruecallerInstalled = truecallerIntent.resolveActivity(packageManager) != null

                            if (isTruecallerInstalled) {
                                context.startActivity(truecallerIntent)
                                Toast.makeText(
                                    context,
                                    "Appel vers ${relative_Client?.nom} via Truecaller",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Fallback to default dialer
                                val defaultDialerIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$phoneNumber")
                                }
                                context.startActivity(defaultDialerIntent)
                                Toast.makeText(
                                    context,
                                    "Appel vers ${relative_Client?.nom}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Impossible de lancer l'appel",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call client",
                    tint = Color(0xFF4CAF50), // Green color for call
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Navigation Icon - only visible if GPS coordinates exist
        if (hasValidLocation) {
            Card(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        val latitude = relative_Client?.latitude ?: 0.0
                        val longitude = relative_Client?.longitude ?: 0.0
                        val clientName = relative_Client?.nom ?: ""

                        try {
                            // Try Google Maps first
                            val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }

                            context.startActivity(mapIntent)
                            Toast.makeText(
                                context,
                                "Navigation vers $clientName",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            // Fallback to generic geo intent
                            try {
                                val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($clientName)")
                                val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri)
                                context.startActivity(fallbackIntent)
                            } catch (e2: Exception) {
                                Toast.makeText(
                                    context,
                                    "Aucune application de navigation disponible",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "Navigate to client",
                    tint = Color(0xFF2196F3), // Blue color for navigation
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Edit Location Icon
        Card(
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    relative_Client?.let { onClickToEditeMarquerPosition(relative_Client) }
                    onDismiss()
                    viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                    focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
                }
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Edit location",
                modifier = Modifier.padding(8.dp)
            )
        }

        // Client Type Mode Toggle Icon
        Card(
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    val newClientTypeMode = when (clientTypeMode) {
                        M2Client.ClientTypeMode.ANCIEN -> M2Client.ClientTypeMode.NEVEAU
                        M2Client.ClientTypeMode.NEVEAU -> M2Client.ClientTypeMode.EVITE
                        M2Client.ClientTypeMode.EVITE -> M2Client.ClientTypeMode.ANCIEN
                        null -> M2Client.ClientTypeMode.NEVEAU
                    }

                    // Update the client's type mode
                    relative_Client?.let { client ->
                        client.clientTypeMode = newClientTypeMode
                        viewModel.updateData(client)
                    }

                    onClientTypeModeChange(newClientTypeMode)
                }
        ) {
            clientTypeMode?.let {
                Icon(
                    imageVector = it.icon,
                    contentDescription = "Toggle Client Type",
                    tint = it.color,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    Card(
        modifier = Modifier
            .clickable { onShowEditDialogChange(true) }
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                modifier = Modifier.getSemanticsTag(relative_Client, ""),
                text = relative_Client?.nom ?: runtime_throw_Erreur_Pour_Regle_Le_Real_Bug("relative_Client?.nom"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (hasPhoneNumber) {
                Text(
                    text = relative_Client.numTelephone ?: "",
                    modifier = Modifier.clickable { onShowPhoneDialogChange(true) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
