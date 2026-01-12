package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.C_Moulahadat_Kadima.T.Dialog

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.String_OutlinedText_Avec_Init_Click_Button_Modulable_Proto4_ForStrings
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddObservationDialog(
    onDismiss: () -> Unit,
    onAdd: (M20ObsarvationEtudion) -> Unit
) {
    var tabrireInput by remember { mutableStateOf("") }
    val currentDate = remember {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة سجل جديد") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "التاريخ: $currentDate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                String_OutlinedText_Avec_Init_Click_Button_Modulable_Proto4_ForStrings(
                    start_text = tabrireInput,
                    placeholder = "تبرير الغياب (اختياري)",
                    icon = null,
                    isAvailable = true,
                    compact_taille = false,
                    on_DonneClick_Data_Update = { newValue ->
                        tabrireInput = newValue
                    }
                )

                Text(
                    text = "• إذا تركت الحقل فارغاً: سيتم إضافة سجل غياب بدون تبرير\n• إذا أدخلت تبريراً: سيتم إضافة سجل غياب مع التبرير",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newObservation = M20ObsarvationEtudion(
                        type = M20ObsarvationEtudion.Type.Raeeb,
                        tabrire_riyab = tabrireInput.trim(),
                        sessionDateTimestamp = System.currentTimeMillis(),
                        creationTimestamps = System.currentTimeMillis()
                    )
                    onAdd(newObservation)
                }
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
