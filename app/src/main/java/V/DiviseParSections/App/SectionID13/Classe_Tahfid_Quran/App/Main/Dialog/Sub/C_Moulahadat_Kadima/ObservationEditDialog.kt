package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.C_Moulahadat_Kadima

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.SouraSelectionDialog
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.TakiyimSelectionDialog
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ObservationEditDialog(
    observation: M20ObsarvationEtudion,
    onDismiss: () -> Unit,
    onSave: (M20ObsarvationEtudion) -> Unit
) {
    var minSoura by remember { mutableStateOf(observation.min_soura) }
    var minAya by remember { mutableStateOf(observation.min_aya.toString()) }
    var ilaSoura by remember { mutableStateOf(observation.ila_soura) }
    var ilaAya by remember { mutableStateOf(observation.ila_aya.toString()) }
    var takyim by remember { mutableStateOf(observation.takyim) }
    var tikrar by remember { mutableStateOf(observation.tikrar.toString()) }
    var el3arde by remember { mutableStateOf(observation.el3arde.toString()) }

    // Store the selected errors - initialized from THIS observation, not history
    var selectedErrors by remember {
        mutableStateOf(observation.getMoulahadatList().toSet())
    }

    var showMinSouraDialog by remember { mutableStateOf(false) }
    var showIlaSouraDialog by remember { mutableStateOf(false) }
    var showTakiyimDialog by remember { mutableStateOf(false) }

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
                    text = "✏️ تعديل السجل",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Divider()

                // From Sura Section
                Text(
                    text = "من سورة:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMinSouraDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "السورة:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = minSoura.arabicName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                OutlinedTextField(
                    value = minAya,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            minAya = newValue
                        }
                    },
                    label = { Text("رقم الآية (من)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                // To Sura Section
                Text(
                    text = "إلى سورة:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showIlaSouraDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "السورة:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = ilaSoura.arabicName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                OutlinedTextField(
                    value = ilaAya,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            ilaAya = newValue
                        }
                    },
                    label = { Text("رقم الآية (إلى)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                Divider()

                // Takyim Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTakiyimDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "التقييم:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = takyim.arabicName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                // Display selected errors
                if (selectedErrors.isNotEmpty()) {
                    Divider()

                    Text(
                        text = "ملاحظات للإصلاح:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        selectedErrors.forEach { error ->
                            Text(
                                text = "• ${error.bil_3arabiya}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }

                Divider()

                // Optional fields
                OutlinedTextField(
                    value = tikrar,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            tikrar = newValue
                        }
                    },
                    label = { Text("التكرار") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = el3arde,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            el3arde = newValue
                        }
                    },
                    label = { Text("العرض") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )

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
                            val updatedObservation = observation.copy(
                                min_soura = minSoura,
                                min_aya = minAya.toIntOrNull() ?: observation.min_aya,
                                ila_soura = ilaSoura,
                                ila_aya = ilaAya.toIntOrNull() ?: observation.ila_aya,
                                takyim = takyim,
                                tikrar = tikrar.toIntOrNull() ?: observation.tikrar,
                                el3arde = el3arde.toIntOrNull() ?: observation.el3arde,
                                moulahadat_takyim_li_islahiha = M20ObsarvationEtudion.Moulahadat_Akhtae_Hifd.listToString(selectedErrors.toList()),
                                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                            )
                            onSave(updatedObservation)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("حفظ")
                    }
                }
            }
        }
    }

    // Selection Dialogs
    if (showMinSouraDialog) {
        SouraSelectionDialog(
            currentSoura = minSoura,
            onDismiss = { showMinSouraDialog = false },
            onSelect = { selected ->
                minSoura = selected
                showMinSouraDialog = false
            }
        )
    }

    if (showIlaSouraDialog) {
        SouraSelectionDialog(
            currentSoura = ilaSoura,
            onDismiss = { showIlaSouraDialog = false },
            onSelect = { selected ->
                ilaSoura = selected
                showIlaSouraDialog = false
            }
        )
    }

    if (showTakiyimDialog) {
        TakiyimSelectionDialog(
            currentTakiyim = takyim,
            etudiantKeyID = null,  // Don't pre-populate from history when editing
            onDismiss = { showTakiyimDialog = false },
            onSelect = { selectedTakiyim, errors ->
                takyim = selectedTakiyim
                selectedErrors = errors.toSet()
                showTakiyimDialog = false
            }
        )
    }
}
