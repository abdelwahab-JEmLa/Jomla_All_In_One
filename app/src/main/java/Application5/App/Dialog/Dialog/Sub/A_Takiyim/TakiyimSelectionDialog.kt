package Application5.App.Dialog.Dialog.Sub.A_Takiyim

import Application5.App.Repository.M19Etudiant
import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import EntreApps.Shared.Models.Compts
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
fun TakiyimSelectionDialog_SeparatedAppsCodingPattern(
    currentTakiyim: M19Etudiant.Takiyim,
    etudiantKeyID: String? = null,
    onDismiss: () -> Unit,
    onSelect: (M19Etudiant.Takiyim, List<String>) -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    var selectedTakiyim by remember { mutableStateOf(currentTakiyim) }

    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val activeOusstad = activeCentralValues.active_Ousstad_Tahfid

    var showTakiyimDropdown by remember { mutableStateOf(false) }

    // Get the most recent observation for this student to pre-populate moulahadat
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

    // Get teacher's key from active Ousstad or from latest observation
    val teacherKeyID = remember(activeOusstad) {
        val params = M00CentralParametresOfAllApps()
        when (activeOusstad) {
            Ousstad_Tahfid.Abdelwahab_Osstad ->Compts.AbdelwahabTravailleChezGros_KeyId.keyId
            Ousstad_Tahfid.Amine_Madrassa -> params.amine_madrasa_Compt_KeyId
            Ousstad_Tahfid.Kissm_Intikali -> "Kissm_Intikali"
            Ousstad_Tahfid.Non_Defini_Actuellemen -> "Non_Defini_Actuellemen"
            null -> latestObservation?.parent_ousstad_key ?: ""
        }
    }

    // Get sorted moulahadat (teacher's first, most recent, then others)
    val sortedMoulahadat by remember(teacherKeyID) {
        derivedStateOf {
            aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion
                .getSortedMoulahadatForTeacher(teacherKeyID)
        }
    }

    // Initialize selected moulahadat from the latest observation
    var selectedMoulahadat by remember(latestObservation) {
        mutableStateOf<Set<String>>(
            latestObservation?.getMoulahadatList()?.toSet() ?: emptySet()
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newMoulahadaText by remember { mutableStateOf("") }

    var showEditDialog by remember { mutableStateOf(false) }
    var editingMoulahada by remember { mutableStateOf("") }
    var editedMoulahadaText by remember { mutableStateOf("") }

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

                Text(
                    text = "التقييم:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Box {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTakiyimDropdown = true },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedTakiyim.arabicName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "اختر",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showTakiyimDropdown,
                        onDismissRequest = { showTakiyimDropdown = false }
                    ) {
                        M19Etudiant.Takiyim.values().forEach { takiyim ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = takiyim.arabicName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selectedTakiyim == takiyim) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                },
                                onClick = {
                                    selectedTakiyim = takiyim
                                    showTakiyimDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider()

                // Moulahadat section (only if not "Lam_Yahfed")
                if (selectedTakiyim != M19Etudiant.Takiyim.Lam_Yahfed) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ملاحظات للإصلاح:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        IconButton(
                            onClick = { showAddDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "إضافة ملاحظة",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Show indicator if moulahadat are pre-populated
                    if (latestObservation != null && selectedMoulahadat.isNotEmpty()) {
                        Text(
                            text = "✓ من السجل السابق",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Show active teacher info
                    if (activeOusstad != null) {
                        Text(
                            text = "الأستاذ: ${activeOusstad.nom_arab}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display sorted moulahadat from database
                    if (sortedMoulahadat.isNotEmpty()) {
                        Text(
                            text = "اختر من الملاحظات السابقة:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        sortedMoulahadat.forEach { moulahada ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedMoulahadat = if (selectedMoulahadat.contains(moulahada)) {
                                                selectedMoulahadat - moulahada
                                            } else {
                                                selectedMoulahadat + moulahada
                                            }
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedMoulahadat.contains(moulahada),
                                        onCheckedChange = { checked ->
                                            selectedMoulahadat = if (checked) {
                                                selectedMoulahadat + moulahada
                                            } else {
                                                selectedMoulahadat - moulahada
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = moulahada,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                // Edit button for each moulahada
                                IconButton(
                                    onClick = {
                                        editingMoulahada = moulahada
                                        editedMoulahadaText = moulahada
                                        showEditDialog = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "تعديل",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Display newly added moulahadat (not yet in database)
                    val newMoulahadat = selectedMoulahadat.filter { it !in sortedMoulahadat }
                    if (newMoulahadat.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ملاحظات جديدة:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        newMoulahadat.forEach { moulahada ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = moulahada,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            selectedMoulahadat = selectedMoulahadat - moulahada
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "حذف",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Clear moulahadat if "Lam_Yahfed" is selected
                    LaunchedEffect(selectedTakiyim) {
                        selectedMoulahadat = emptySet()
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
                            onSelect(selectedTakiyim, selectedMoulahadat.toList())
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("حفظ")
                    }
                }
            }
        }
    }

    // Add new moulahada dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                newMoulahadaText = ""
            },
            title = { Text("إضافة ملاحظة جديدة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Show active teacher info
                    if (activeOusstad != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = "سيتم إضافة الملاحظة للأستاذ: ${activeOusstad.nom_arab}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = newMoulahadaText,
                        onValueChange = { newMoulahadaText = it },
                        label = { Text("الملاحظة") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMoulahadaText.isNotBlank()) {
                            selectedMoulahadat = selectedMoulahadat + newMoulahadaText.trim()
                            newMoulahadaText = ""
                            showAddDialog = false
                        }
                    },
                    enabled = newMoulahadaText.isNotBlank()
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        newMoulahadaText = ""
                    }
                ) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Edit moulahada dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                editingMoulahada = ""
                editedMoulahadaText = ""
            },
            title = { Text("تعديل الملاحظة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "الملاحظة الأصلية:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = editingMoulahada,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedMoulahadaText,
                        onValueChange = { editedMoulahadaText = it },
                        label = { Text("الملاحظة الجديدة") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )
                    Text(
                        text = "⚠️ سيتم تحديث جميع السجلات التي تحتوي على هذه الملاحظة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedMoulahadaText.isNotBlank() && editedMoulahadaText != editingMoulahada) {
                            // Update globally in all observations
                            aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion
                                .updateMoulahadaGlobally(editingMoulahada, editedMoulahadaText.trim())

                            // Update in selected moulahadat
                            selectedMoulahadat = selectedMoulahadat.map {
                                if (it == editingMoulahada) editedMoulahadaText.trim() else it
                            }.toSet()

                            showEditDialog = false
                            editingMoulahada = ""
                            editedMoulahadaText = ""
                        }
                    },
                    enabled = editedMoulahadaText.isNotBlank() && editedMoulahadaText != editingMoulahada
                ) {
                    Text("تحديث الكل")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        editingMoulahada = ""
                        editedMoulahadaText = ""
                    }
                ) {
                    Text("إلغاء")
                }
            }
        )
    }
}
