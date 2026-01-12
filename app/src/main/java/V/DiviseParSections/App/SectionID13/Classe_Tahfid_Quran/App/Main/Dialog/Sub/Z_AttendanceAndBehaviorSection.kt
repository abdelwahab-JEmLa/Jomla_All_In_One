package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Sections

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.ClickableFieldWithIcon
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.EditableField
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp

@Composable
fun AttendanceAndBehaviorSection(
    etudiant: M19Etudiant,
    repo19Etudiant: Repo19Etudiant,
    observations: List<M20ObsarvationEtudion>,
    isEditingQuestionOuiNon: Boolean,
    questionOuiNonInput: String,
    onQuestionOuiNonInputChange: (String) -> Unit,
    onQuestionOuiNonEditClick: () -> Unit,
    onQuestionOuiNonSave: () -> Unit,
    questionOuiNonFocusRequester: FocusRequester,
    onShowMoulahada3alaSouloukDialog: () -> Unit,
    isEditingMoulahada: Boolean,
    moulahadaInput: String,
    onMoulahadaInputChange: (String) -> Unit,
    onMoulahadaEditClick: () -> Unit,
    onMoulahadaSave: () -> Unit,
    moulahadaFocusRequester: FocusRequester
) {
    // FIXED: Calculate absences from observations
    val absenceCount = remember(etudiant, observations) {
        etudiant.calculateUnjustifiedAbsences(observations)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // FIXED: Display calculated absences (read-only, calculated from observations)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "الغياب بدون مبرر:", style = MaterialTheme.typography.bodyMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Display calculated count (read-only)
                Text(
                    text = absenceCount.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (absenceCount > 0) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                // Print toggle button
                IconButton(
                    onClick = {
                        repo19Etudiant.upsert(
                            etudiant.copy(imprime_justification = !etudiant.imprime_justification)
                        )
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "طباعة المبرر",
                        tint = if (etudiant.imprime_justification) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        }
                    )
                }
            }
        }

        Text(
            text = "ℹ️ يتم حساب الغياب تلقائياً من الملاحظات",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Question Oui/Non (editable)
        EditableField(
            label = "سؤال نعم/لا:",
            value = etudiant.question_par_non,
            isEditing = isEditingQuestionOuiNon,
            inputValue = questionOuiNonInput,
            onInputChange = onQuestionOuiNonInputChange,
            onEditClick = onQuestionOuiNonEditClick,
            onSave = onQuestionOuiNonSave,
            focusRequester = questionOuiNonFocusRequester,
            textStyle = MaterialTheme.typography.bodySmall,
            width = 150.dp
        )

        // Moulahada 3ala Soulouk
        ClickableFieldWithIcon(
            label = "ملاحظة على السلوك:",
            value = etudiant.moulahada_3ala_soulouk.arabicName,
            onClick = onShowMoulahada3alaSouloukDialog,
            color = MaterialTheme.colorScheme.tertiary
        )

        // Moulahada Makouba (editable)
        EditableField(
            label = "ملاحظة مكتوبة:",
            value = etudiant.moulahada_makouba,
            isEditing = isEditingMoulahada,
            inputValue = moulahadaInput,
            onInputChange = onMoulahadaInputChange,
            onEditClick = onMoulahadaEditClick,
            onSave = onMoulahadaSave,
            focusRequester = moulahadaFocusRequester,
            textStyle = MaterialTheme.typography.bodySmall,
            width = 150.dp
        )
    }
}
