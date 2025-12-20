package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.FastEdite_OutlinedTextField_V2
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

@SuppressLint("DefaultLocale")
@Composable
fun Section_Edit_Fraitspre_fraits_voiture_essance_marche_et_paprasse(
    relative_M14VentPeriode: M14VentPeriode,
    editingField: String?,
    editingValue: String,
    onStartEditing: (String, Double) -> Unit,
    onEditingValueChange: (String) -> Unit,
    onSaveEditedValue: () -> Unit,
    focusRequester: FocusRequester
) {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Frais (Voiture, Essence, Marché, Papiers)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            FastEdite_OutlinedTextField_V2(
                label = "Frais totaux:",
                value = String.format(
                    "%.2f DA",
                    relative_M14VentPeriode.pre_fraits_voiture_essance_marche_et_paprasse
                ),
                isEditing = editingField == "pre_fraits",
                inputValue = editingValue,
                onInputChange = onEditingValueChange,
                onEditClick = {
                    onStartEditing(
                        "pre_fraits",
                        relative_M14VentPeriode.pre_fraits_voiture_essance_marche_et_paprasse
                    )
                },
                onSave = onSaveEditedValue,
                focusRequester = focusRequester
            )
        }
    }
}
