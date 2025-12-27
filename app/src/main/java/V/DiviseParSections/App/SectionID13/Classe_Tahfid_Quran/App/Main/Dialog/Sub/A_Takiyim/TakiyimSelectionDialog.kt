package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.koin.compose.koinInject

@Composable
fun TakiyimSelectionDialog(
    currentTakiyim: M19Etudiant.Takiyim,
    etudiantKeyID: String? = null,
    onDismiss: () -> Unit,
    onSelect: (M19Etudiant.Takiyim, List<M20ObsarvationEtudion.Moulahadat_Akhtae_Hifd>) -> Unit,
    aCentralFacade: ACentralFacade = koinInject()
) {
    var selectedTakiyim by remember { mutableStateOf(currentTakiyim) }

    // Get the most recent observation for this student to pre-populate errors
    val latestObservation by remember(etudiantKeyID) {
        derivedStateOf {
            if (etudiantKeyID != null) {
                aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion.datasValue
                    .filter { it.etudiant_keyID == etudiantKeyID }
                    .maxByOrNull { it.creationTimestamps }
            } else {
                null
            }
        }
    }

    // Initialize selected errors from the latest observation
    var selectedErrors by remember(latestObservation) {
        mutableStateOf<Set<M20ObsarvationEtudion.Moulahadat_Akhtae_Hifd>>(
            latestObservation?.getMoulahadatList()?.toSet() ?: emptySet()
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Text(
                    text = "⭐ اختر التقييم",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Divider()

                // Takiyim options
                Text(
                    text = "التقييم:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                M19Etudiant.Takiyim.values().forEach { takiyim ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTakiyim = takiyim },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTakiyim == takiyim) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = if (selectedTakiyim == takiyim) {
                            androidx.compose.foundation.BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.primary
                            )
                        } else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = takiyim.arabicName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selectedTakiyim == takiyim) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )

                            RadioButton(
                                selected = selectedTakiyim == takiyim,
                                onClick = { selectedTakiyim = takiyim }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider()

                // Error checkboxes (only if not "Lam_Yahfed")
                if (selectedTakiyim != M19Etudiant.Takiyim.Lam_Yahfed) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ملاحظات للإصلاح (اختياري):",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        // Show indicator if errors are pre-populated
                        if (latestObservation != null && selectedErrors.isNotEmpty()) {
                            Text(
                                text = "من السجل السابق",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    Text(
                        text = "اختر الأخطاء التي يجب إصلاحها:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    M20ObsarvationEtudion.Moulahadat_Akhtae_Hifd.values().forEach { error ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedErrors = if (selectedErrors.contains(error)) {
                                        selectedErrors - error
                                    } else {
                                        selectedErrors + error
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedErrors.contains(error),
                                onCheckedChange = { checked ->
                                    selectedErrors = if (checked) {
                                        selectedErrors + error
                                    } else {
                                        selectedErrors - error
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error.bil_3arabiya,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    // Clear errors if "Lam_Yahfed" is selected
                    LaunchedEffect(selectedTakiyim) {
                        selectedErrors = emptySet()
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            onSelect(selectedTakiyim, selectedErrors.toList())
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("حفظ")
                    }
                }
            }
        }
    }
}
