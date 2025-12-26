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
fun MemorizationProgramSection(
    etudiant: M19Etudiant,
    onShowSouraDialog: () -> Unit,
    isEditingDernierAyaa: Boolean,
    dernierAyaaInput: String,
    onDernierAyaaInputChange: (String) -> Unit,
    onDernierAyaaEditClick: () -> Unit,
    onDernierAyaaSave: () -> Unit,
    dernierAyaaFocusRequester: FocusRequester,
    onShowTakiyimDialog: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "📖 برنامج الحفظ",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Dernier Soura
                ClickableFieldWithIcon(
                    label = "آخر سورة:",
                    value = etudiant.dernier_Soura_Wassale_Laha.arabicName,
                    onClick = onShowSouraDialog,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(4.dp))

                // Dernier Ayaa (editable)
                FastEdite_OutlinedTextField(
                    label = "رقم الآية:",
                    value = etudiant.dernier_Soura_sater.toString(),
                    isEditing = isEditingDernierAyaa,
                    inputValue = dernierAyaaInput,
                    onInputChange = onDernierAyaaInputChange,
                    onEditClick = onDernierAyaaEditClick,
                    onSave = onDernierAyaaSave,
                    focusRequester = dernierAyaaFocusRequester
                )

                Spacer(modifier = Modifier.padding(4.dp))

                // Dernier Takiyim Ijtihad
                ClickableFieldWithIcon(
                    label = "تقييم الاجتهاد:",
                    value = etudiant.dernier_takyim_dabte.arabicName,
                    onClick = onShowTakiyimDialog,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

