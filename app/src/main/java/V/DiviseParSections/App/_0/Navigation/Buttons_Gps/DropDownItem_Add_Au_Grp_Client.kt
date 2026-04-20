package V.DiviseParSections.App._0.Navigation.Buttons_Gps

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun DropDownItem_Add_Au_Grp_Client(
    nomFun: String = "Ajouter Clients Ciblés",
    aCentralFacade: ACentralFacade = koinInject(),
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val repositorysMainGetter = aCentralFacade.repositorysMainGetter
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

    // Count clients with "Cible" state
    val clientsCibleCount = remember(
        repositorysMainGetter.repo8BonVent.datasValue,
        repositorysMainGetter.repo2Client.datasValue
    ) {
        val currentPeriodId = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID

        repositorysMainGetter.repo2Client.datasValue.count { client ->
            val lastBonVent = repositorysMainGetter.repo8BonVent.datasValue
                .filter {
                    it.parent_M2Client_KeyID == client.keyID &&
                            it.parent_M14VentPeriod_KeyId == currentPeriodId
                }
                .maxByOrNull { it.creationTimestamps }

            lastBonVent?.etateActuellementEst == M8BonVent.EtateActuellementEst.Cible
        }
    }

    fun addClientsCibleToGroup() {
        isLoading = true
        scope.launch {
            try {
                // Get current AppCompt
                val currentAppCompt = repositorysMainGetter.repo9AppCompt.datasValue.find {
                    it.keyID == "-OV9dYujH9cA3yEx8AY2"
                }

                if (currentAppCompt == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "❌ Compte application introuvable",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                val currentPeriodId = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID

                if (currentPeriodId == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "❌ Aucune période active",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                // Find all clients with last state = Cible
                val clientsCibleKeys = repositorysMainGetter.repo2Client.datasValue
                    .filter { client ->
                        val lastBonVent = repositorysMainGetter.repo8BonVent.datasValue
                            .filter {
                                it.parent_M2Client_KeyID == client.keyID &&
                                        it.parent_M14VentPeriod_KeyId == currentPeriodId
                            }
                            .maxByOrNull { it.creationTimestamps }

                        lastBonVent?.etateActuellementEst == M8BonVent.EtateActuellementEst.Cible
                    }
                    .map { it.keyID }

                if (clientsCibleKeys.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "ℹ️ Aucun client ciblé trouvé",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                // Get existing keys from comma-separated string
                val existingKeys = currentAppCompt.separeted_by_commas_keys_clients_a_cible_groupe_n1
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .toSet()

                // Merge with new keys (avoid duplicates)
                val allKeys = (existingKeys + clientsCibleKeys).toSet()

                // Create updated comma-separated string
                val updatedKeysString = allKeys.joinToString(",")

                // Update AppCompt
                val updatedAppCompt = currentAppCompt.copy(
                    separeted_by_commas_keys_clients_a_cible_groupe_n1 = updatedKeysString
                )

                repositorysMainSetter.update_M9AppCompt(updatedAppCompt)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "✅ ${clientsCibleKeys.size} client(s) ajouté(s) au groupe",
                        Toast.LENGTH_LONG
                    ).show()
                }

                Log.i("AddClientsCible", "✅ Added ${clientsCibleKeys.size} clients to group")

            } catch (e: Exception) {
                Log.e("AddClientsCible", "❌ Error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "❌ Erreur: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                isLoading = false
            }
        }
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoading) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.GroupAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            text = {
                Text(
                    text = if (isLoading) {
                        "Ajout en cours..."
                    } else {
                        "$nomFun ($clientsCibleCount)"
                    },
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    addClientsCibleToGroup()
                }
            },
            enabled = !isLoading && clientsCibleCount > 0
        )
    }
}
