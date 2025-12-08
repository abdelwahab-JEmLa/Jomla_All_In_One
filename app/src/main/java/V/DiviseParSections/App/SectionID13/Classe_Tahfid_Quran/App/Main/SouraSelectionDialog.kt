package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SOUAR
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
 fun SouraSelectionDialog(
    currentSoura: SOUAR,
    onDismiss: () -> Unit,
    onSelect: (SOUAR) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.Companion.fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier.Companion.fillMaxSize()
            ) {
                Text(
                    text = "اختر السورة",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.Companion.padding(16.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.Companion.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SOUAR.entries.size) { index ->
                        val soura = SOUAR.entries[index]
                        Card(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .clickable { onSelect(soura) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (soura == currentSoura)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = soura.arabicName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.Companion
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Companion.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
