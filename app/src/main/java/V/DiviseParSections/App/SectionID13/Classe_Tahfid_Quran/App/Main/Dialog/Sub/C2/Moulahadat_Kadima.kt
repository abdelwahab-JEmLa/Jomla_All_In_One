package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Sections

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.C2.QuickObservationSummary
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.C2.TamaHistoryDialog
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Moulahadat_Kadima(
    etudiant: M19Etudiant,
    onShowMokarrareDialog: () -> Unit,
    isEditingMokarrareAyaa: Boolean,
    mokarrareAyaaInput: String,
    onMokarrareAyaaInputChange: (String) -> Unit,
    onMokarrareAyaaEditClick: () -> Unit,
    onMokarrareAyaaSave: () -> Unit,
    mokarrareAyaaFocusRequester: FocusRequester,
    isEditingTikrare: Boolean,
    tikrareInput: String,
    onTikrareInputChange: (String) -> Unit,
    onTikrareEditClick: () -> Unit,
    onTikrareSave: () -> Unit,
    tikrareFocusRequester: FocusRequester,
    isEditingTikrar3ard: Boolean,
    tikrar3ardInput: String,
    onTikrar3ardInputChange: (String) -> Unit,
    onTikrar3ardEditClick: () -> Unit,
    onTikrar3ardSave: () -> Unit,
    tikrar3ardFocusRequester: FocusRequester,
    aCentralFacade: ACentralFacade = koinInject()
) {
    // TODO(1) FIXED: Get previous observations for THIS SPECIFIC STUDENT only
    val repo20 = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion

    val studentObservations by remember(etudiant.keyID) {
        derivedStateOf {
            repo20.datasValue
                .filter { it.etudiant_keyID == etudiant.keyID } // Filter by student ID
                .sortedByDescending { it.creationTimestamps }
        }
    }

    var showHistoryDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "🔄 البرنامج قبل الحالي", // TODO(1) FIXED: Changed title
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Button to show full history dialog
        if (studentObservations.isNotEmpty()) {
            Button(
                onClick = { showHistoryDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "التاريخ",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text("عرض تاريخ التمام الكامل (${studentObservations.size})")
            }

            Spacer(modifier = Modifier.padding(4.dp))
        }

        // TODO(1) FIXED: Display previous observations
        if (studentObservations.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "آخر 3 سجلات",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    studentObservations.take(3).forEach { observation ->
                        QuickObservationSummary(observation)
                        Spacer(modifier = Modifier.padding(2.dp))
                    }

                    if (studentObservations.size > 3) {
                        Text(
                            text = "... و ${studentObservations.size - 3} سجلات أخرى",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }

    // History Dialog
    if (showHistoryDialog) {
        TamaHistoryDialog(
            observations = studentObservations,
            onDismiss = { showHistoryDialog = false },
            onEdit = { updatedObs ->
                aCentralFacade.repositorysMainSetter.upsert_M20ObsarvationEtudion(updatedObs)
            }
        )
    }
}
