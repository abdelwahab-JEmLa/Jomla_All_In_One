package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.FastEdite_OutlinedTextField_V2
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
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
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun Section_Edit_Fraitspre_fraits_voiture_essance_marche_et_paprasse(
    relative_M14VentPeriode: M14VentPeriode,
    onUpdate: (M14VentPeriode) -> Unit  // Nouveau paramètre
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Frais (Voiture, Essence)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            FastEdite_OutlinedTextField_V2(
                label = "Frais totaux:",
                value = relative_M14VentPeriode.pre_fraits_voiture_essance_marche_et_paprasse,
                onSave = { newValue ->
                    val updatedPeriode = relative_M14VentPeriode.copy(
                        pre_fraits_voiture_essance_marche_et_paprasse = newValue
                    )
                    onUpdate(updatedPeriode)
                }
            )
        }
    }
}
