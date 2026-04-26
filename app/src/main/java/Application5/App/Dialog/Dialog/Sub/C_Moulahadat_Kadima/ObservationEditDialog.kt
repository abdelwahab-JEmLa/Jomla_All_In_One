package Application5.App.Dialog.Dialog.Sub.C_Moulahadat_Kadima

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.A_Takiyim.TakiyimSelectionDialog
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.SouraSelectionDialog
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.String_OutlinedText_Avec_Init_Click_Button_Modulable_Proto4_ForStrings
import Application5.App.Repository.M20ObsarvationEtudion
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ObservationEditDialog_SeparatedAppsCodingPattern(
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

    // NEW: Add field for date/time editing
    var creationDate by remember { mutableStateOf(observation.creationTimestamps) }
    var dateInput by remember {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        mutableStateOf(dateFormat.format(Date(observation.creationTimestamps)))
    }

    // NEW: Add field for tabrire (justification)
    var tabrire by remember { mutableStateOf(observation.tabrire_riyab) }

    // NEW: Track observation type (can be toggled)
    var observationType by remember { mutableStateOf(observation.type) }

    // Store the selected moulahadat - initialized from THIS observation, not history
    var selectedMoulahadat by remember {
        mutableStateOf(observation.getMoulahadatList().toSet())
    }

    var showMinSouraDialog by remember { mutableStateOf(false) }
    var showIlaSouraDialog by remember { mutableStateOf(false) }
    var showTakiyimDialog by remember { mutableStateOf(false) }

    // Track if absence type
    val isAbsence = observationType == M20ObsarvationEtudion.Type.Raeeb

    // Determine if absence is justified
    val isJustified = tabrire.isNotBlank()

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

                // NEW: Type toggle (Raeeb/Layare)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAbsence) "غياب (Raeeb)" else "حضور (Layare)",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isAbsence) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )

                    Switch(
                        checked = isAbsence,
                        onCheckedChange = { isChecked ->
                            observationType = if (isChecked) {
                                M20ObsarvationEtudion.Type.Raeeb
                            } else {
                                M20ObsarvationEtudion.Type.Tama_Hifdoha
                            }
                            // Clear justification when switching to non-absence
                            if (!isChecked) {
                                tabrire = ""
                            }
                        }
                    )
                }

                Text(
                    text = if (isAbsence) "تبديل لتسجيل حضور" else "تبديل لتسجيل غياب",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Divider()

                // NEW: Date editing field
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { newValue ->
                        dateInput = newValue
                        // Try to parse the date in format dd.MM.yyyy
                        try {
                            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            val parsedDate = dateFormat.parse(newValue)
                            if (parsedDate != null) {
                                creationDate = parsedDate.time
                            }
                        } catch (e: Exception) {
                            // Keep the old date if parsing fails
                        }
                    },
                    label = { Text("التاريخ (dd.MM.yyyy)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    supportingText = {
                        Text(
                            text = "مثال: 04.01.2026",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )

                Divider()

                // NEW: Smart justification field if absence
                if (isAbsence) {
                    Text(
                        text = "تبرير الغياب:",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isJustified) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )

                    // Use the smart text field component
                    String_OutlinedText_Avec_Init_Click_Button_Modulable_Proto4_ForStrings(
                        start_text = tabrire,
                        placeholder = "اضغط للتبرير",
                        icon = Icons.Default.Edit,
                        isAvailable = true,
                        compact_taille = false,
                        modifier = Modifier.fillMaxWidth(),
                        on_DonneClick_Data_Update = { newValue ->
                            tabrire = newValue
                        }
                    )

                    if (!isJustified) {
                        Text(
                            text = "⚠️ غياب غير مبرر",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Divider()
                }

                // From Sura Section (only if not absence)
                if (!isAbsence) {
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
                }

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

                // Display selected moulahadat
                if (selectedMoulahadat.isNotEmpty()) {
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
                        selectedMoulahadat.forEach { moulahada ->
                            Text(
                                text = "• $moulahada",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }

                Divider()

                // Optional fields (only if not absence)
                if (!isAbsence) {
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
                            val updatedObservation = observation.copy(
                                type = observationType, // Save the updated type
                                min_soura = minSoura,
                                min_aya = minAya.toIntOrNull() ?: observation.min_aya,
                                ila_soura = ilaSoura,
                                ila_aya = ilaAya.toIntOrNull() ?: observation.ila_aya,
                                takyim = takyim,
                                tikrar = tikrar.toIntOrNull() ?: observation.tikrar,
                                el3arde = el3arde.toIntOrNull() ?: observation.el3arde,
                                moulahadat_takyim_li_islahiha = selectedMoulahadat.joinToString(","),
                                tabrire_riyab = tabrire,
                                creationTimestamps = creationDate,
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
            etudiantKeyID = null,
            onDismiss = { showTakiyimDialog = false },
            onSelect = { selectedTakiyim, moulahadat ->
                takyim = selectedTakiyim
                selectedMoulahadat = moulahadat.toSet()
                showTakiyimDialog = false
            }
        )
    }
}
