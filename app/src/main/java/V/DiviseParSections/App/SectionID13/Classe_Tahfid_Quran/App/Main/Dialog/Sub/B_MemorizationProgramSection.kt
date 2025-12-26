package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.ClickableFieldWithIcon
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.FastEdite_OutlinedTextField
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Moukarar(
    etudiant: M19Etudiant,
    onShowSouraDialog: () -> Unit,
    isEditingDernierAyaa: Boolean,
    dernierAyaaInput: String,
    onDernierAyaaInputChange: (String) -> Unit,
    onDernierAyaaEditClick: () -> Unit,
    onDernierAyaaSave: () -> Unit,
    dernierAyaaFocusRequester: FocusRequester,
    onShowTakiyimDialog: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject()
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "من سورة:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    ClickableFieldWithIcon(
                        label = "",
                        value = etudiant.dernier_Soura_Wassale_Laha.arabicName,
                        onClick = onShowSouraDialog,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.padding(2.dp))

                // Dernier Ayaa (editable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الآية:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FastEdite_OutlinedTextField(
                        label = "",
                        value = etudiant.dernier_Soura_sater.toString(),
                        isEditing = isEditingDernierAyaa,
                        inputValue = dernierAyaaInput,
                        onInputChange = onDernierAyaaInputChange,
                        onEditClick = onDernierAyaaEditClick,
                        onSave = onDernierAyaaSave,
                        focusRequester = dernierAyaaFocusRequester
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                // "To" section - Mokarrare range
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إلى سورة:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = etudiant.mokarrare_hifde.arabicName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.padding(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "رقمها:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = etudiant.mokarrare_hifde_sater.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
