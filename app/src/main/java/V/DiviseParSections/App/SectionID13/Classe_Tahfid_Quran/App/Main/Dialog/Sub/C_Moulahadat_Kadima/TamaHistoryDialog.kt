package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.C_Moulahadat_Kadima

import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun TamaHistoryDialog(
    observations: List<M20ObsarvationEtudion>,
    onDismiss: () -> Unit,
    onEdit: (M20ObsarvationEtudion) -> Unit = {},
    onDelete: (M20ObsarvationEtudion) -> Unit = {}
) {
    var editingObservation by remember { mutableStateOf<M20ObsarvationEtudion?>(null) }
    var deletingObservation by remember { mutableStateOf<M20ObsarvationEtudion?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 تاريخ التمام",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "${observations.size} سجل",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                // Message if no observations
                if (observations.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا يوجد سجلات بعد",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Scrollable list of observations
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        observations.forEach { observation ->
                            ObservationCard(
                                observation = observation,
                                onEdit = { editingObservation = it },
                                onDelete = { deletingObservation = it }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إغلاق")
                }
            }
        }
    }

    // Edit Dialog
    editingObservation?.let { obs ->
        ObservationEditDialog(
            observation = obs,
            onDismiss = { editingObservation = null },
            onSave = { updated ->
                onEdit(updated)
                editingObservation = null
            }
        )
    }

    // Delete Confirmation Dialog
    deletingObservation?.let { obs ->
        AlertDialog(
            onDismissRequest = { deletingObservation = null },
            title = { Text("تأكيد الحذف") },
            text = { Text("هل أنت متأكد من حذف هذا السجل؟ لا يمكن التراجع عن هذا الإجراء.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(obs)
                        deletingObservation = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingObservation = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
