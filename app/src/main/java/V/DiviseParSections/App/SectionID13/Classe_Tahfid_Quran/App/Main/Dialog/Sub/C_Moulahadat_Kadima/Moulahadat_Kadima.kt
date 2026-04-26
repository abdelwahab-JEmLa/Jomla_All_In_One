package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Sections

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.C_Moulahadat_Kadima.QuickObservationSummary
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.C_Moulahadat_Kadima.T.Dialog.TamaHistoryDialog
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import Application5.App.Repository.M19Etudiant
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
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Moulahadat_Kadima(
    etudiant: M19Etudiant,
    aCentralFacade: ACentralFacade = koinInject()
) {
    // Get previous observations for THIS SPECIFIC STUDENT only
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
            text = "📄 البرنامج قبل الحالي",
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

        // Display previous observations - FIXED: Now shows 4 records
        if (studentObservations.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "آخر 4 سجلات",  // FIXED: Changed from 3 to 4
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    studentObservations.take(4).forEach { observation ->  // FIXED: Changed from 3 to 4
                        QuickObservationSummary(observation)
                        Spacer(modifier = Modifier.padding(2.dp))
                    }

                    if (studentObservations.size > 4) {  // FIXED: Changed from 3 to 4
                        Text(
                            text = "... و ${studentObservations.size - 4} سجلات أخرى",  // FIXED: Changed from 3 to 4
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }

    // History Dialog with Edit and Delete functionality
    if (showHistoryDialog) {
        TamaHistoryDialog(
            observations = studentObservations,
            onDismiss = { showHistoryDialog = false },
            onEdit = { updatedObs ->
                aCentralFacade.repositorysMainSetter.upsert_M20ObsarvationEtudion(updatedObs)
            },
            onDelete = { obsToDelete ->
                repo20.delete(obsToDelete)
            }
        )
    }
}
