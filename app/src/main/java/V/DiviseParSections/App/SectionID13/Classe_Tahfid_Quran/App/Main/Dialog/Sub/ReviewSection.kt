package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Sections

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.ClickableFieldWithIcon
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.FastEdite_OutlinedTextField
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp

@Composable
fun ReviewSection(
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
    tikrar3ardFocusRequester: FocusRequester
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "🔄 المراجعة",     //<--
            //TODO(1): change au البرنامج قبل الحالي 
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(vertical = 4.dp)
        )
              //<--
              //TODO(1): fait ici de affiche 
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Mokarrare Hifde
                ClickableFieldWithIcon(
                    label = "مكررة:",
                    value = etudiant.mokarrare_hifde.arabicName,
                    onClick = onShowMokarrareDialog,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.padding(4.dp))

                // Mokarrare Ayaa (editable)
                FastEdite_OutlinedTextField(
                    label = "رقم الآية:",
                    value = etudiant.mokarrare_hifde_sater.toString(),
                    isEditing = isEditingMokarrareAyaa,
                    inputValue = mokarrareAyaaInput,
                    onInputChange = onMokarrareAyaaInputChange,
                    onEditClick = onMokarrareAyaaEditClick,
                    onSave = onMokarrareAyaaSave,
                    focusRequester = mokarrareAyaaFocusRequester
                )

                Spacer(modifier = Modifier.padding(4.dp))

                // Tikrare (editable)
                FastEdite_OutlinedTextField(
                    label = "تكرار:",
                    value = etudiant.tikrare.toString(),
                    isEditing = isEditingTikrare,
                    inputValue = tikrareInput,
                    onInputChange = onTikrareInputChange,
                    onEditClick = onTikrareEditClick,
                    onSave = onTikrareSave,
                    focusRequester = tikrareFocusRequester
                )

                Spacer(modifier = Modifier.padding(4.dp))

                // Tikrar 3ard (editable)
                FastEdite_OutlinedTextField(
                    label = "تكرار عرض:",
                    value = etudiant.tikrare_3arde.toString(),
                    isEditing = isEditingTikrar3ard,
                    inputValue = tikrar3ardInput,
                    onInputChange = onTikrar3ardInputChange,
                    onEditClick = onTikrar3ardEditClick,
                    onSave = onTikrar3ardSave,
                    focusRequester = tikrar3ardFocusRequester
                )
            }
        }
    }
}
