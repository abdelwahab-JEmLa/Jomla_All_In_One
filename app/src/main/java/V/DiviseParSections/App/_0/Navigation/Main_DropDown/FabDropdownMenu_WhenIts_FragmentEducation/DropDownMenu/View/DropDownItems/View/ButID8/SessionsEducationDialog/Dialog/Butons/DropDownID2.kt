package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog.Butons

import Application5.App.Repository.SessionDate
import Application5.App.Repository.M20ObsarvationEtudion
import Application5.App.Repository.Repo20ObsarvationEtudion
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Deletes all Raeeb (absence) observations for a specific session date
 */
@Composable
fun DropDownID2(
    sessionDate: SessionDate,     //<--
    //(1): regle que ca deele avant date
    repo20Observation: Repo20ObsarvationEtudion,
    sessionObservations: List<M20ObsarvationEtudion>
) {
    val raeebObservations = sessionObservations.filter { 
        it.type == M20ObsarvationEtudion.Type.Raeeb 
    }

    if (raeebObservations.isEmpty()) {
        return
    }

    OutlinedButton(
        onClick = {
            raeebObservations.forEach { obs ->
                repo20Observation.delete(obs)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("حذف سجلات الغياب فقط (${raeebObservations.size})")
    }
}

/**
 * Deletes all observations before the selected session date
 */
@Composable
fun DeleteObservationsBeforeDateButton(
    sessionDate: SessionDate,
    repo20Observation: Repo20ObsarvationEtudion
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("تأكيد الحذف") },
            text = {
                Text("هل أنت متأكد من حذف جميع السجلات قبل هذا التاريخ؟ هذا الإجراء لا يمكن التراجع عنه.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val observationsToDelete = repo20Observation.datasValue.filter { obs ->
                            obs.sessionDateTimestamp < sessionDate.timestamp
                        }

                        observationsToDelete.forEach { obs ->
                            repo20Observation.delete(obs)
                        }

                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("تأكيد الحذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    val observationsBeforeDate = repo20Observation.datasValue.count { obs ->
        obs.sessionDateTimestamp < sessionDate.timestamp
    }

    if (observationsBeforeDate == 0) {
        return
    }

    OutlinedButton(
        onClick = { showConfirmDialog = true },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("حذف جميع السجلات قبل هذا التاريخ (${observationsBeforeDate})")
    }
}

