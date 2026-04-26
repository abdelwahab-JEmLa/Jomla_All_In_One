package Application5.App.Dialog.Dialog.Sub

import Application5.App.Repository.M19Etudiant
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.ClickableFieldWithIcon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IstedrakSection_SeparatedAppsCodingPattern(
    etudiant: M19Etudiant,
    onShowIstedrakSouraDialog: () -> Unit,
    onShowIstedrakMokarrareDialog: () -> Unit,
    onShowIstedrakTakiyimDialog: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section header
        Text(
            text = "استدراك قديم (Previous Records)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Istedrak Kadim - Akher Soura
        ClickableFieldWithIcon(
            label = "آخر سورة (قديم):",
            value = etudiant.istedrak_kadim_Akher_Soura_Wassale_Laha.arabicName,
            onClick = onShowIstedrakSouraDialog,
            color = MaterialTheme.colorScheme.primary
        )

        // Istedrak Kadim - Moukarare
        ClickableFieldWithIcon(
            label = "مكررة (قديم):",
            value = etudiant.istedrak_kadim_Moukarare.arabicName,
            onClick = onShowIstedrakMokarrareDialog,
            color = MaterialTheme.colorScheme.secondary
        )

        // Istedrak Kadim - Takyim
        ClickableFieldWithIcon(
            label = "تقييم (قديم):",
            value = etudiant.istedrak_kadim_Takyim_hali.arabicName,
            onClick = onShowIstedrakTakiyimDialog,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}
