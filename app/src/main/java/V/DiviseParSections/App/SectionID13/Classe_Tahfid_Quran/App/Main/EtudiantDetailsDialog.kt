package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import android.text.format.DateUtils.isToday
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun EtudiantDetailsDialog(
    etudiant: M19Etudiant,
    repo19Etudiant: Repo19Etudiant,
    onDismiss: () -> Unit,
    onShowSouraDialog: () -> Unit,
    onShowMokarrareDialog: () -> Unit,
    onShowTakiyimDialog: () -> Unit,
    onShowMoulahada3alaSouloukDialog: () -> Unit,
    onShowIstedrakSouraDialog: () -> Unit,
    onShowIstedrakMokarrareDialog: () -> Unit,
    onShowIstedrakTakiyimDialog: () -> Unit
) {
    val wasUpdatedToday = isToday(etudiant.dernierTimeTampsSynchronisationAvecFireBase)

    var isEditingNom by remember { mutableStateOf(false) }
    var isEditingPrenom by remember { mutableStateOf(false) }
    var isEditingAge by remember { mutableStateOf(false) }
    var isEditingPhone by remember { mutableStateOf(false) }
    var isEditingDernierAyaa by remember { mutableStateOf(false) }
    var isEditingMokarrareAyaa by remember { mutableStateOf(false) }
    var isEditingMoulahada by remember { mutableStateOf(false) }
    var isEditingTikrare by remember { mutableStateOf(false) }
    var isEditingTikrar3ard by remember { mutableStateOf(false) }
    var isEditingPosition by remember { mutableStateOf(false) }
    var isEditingQuestionOuiNon by remember { mutableStateOf(false) }
    var isEditingAbsences by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var nomInput by remember { mutableStateOf("") }
    var prenomInput by remember { mutableStateOf("") }
    var ageInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var dernierAyaaInput by remember { mutableStateOf("") }
    var mokarrareAyaaInput by remember { mutableStateOf("") }
    var moulahadaInput by remember { mutableStateOf("") }
    var tikrareInput by remember { mutableStateOf("") }
    var tikrar3ardInput by remember { mutableStateOf("") }
    var positionInput by remember { mutableStateOf("") }
    var questionOuiNonInput by remember { mutableStateOf("") }
    var absencesInput by remember { mutableStateOf("") }

    val nomFocusRequester = remember { FocusRequester() }
    val prenomFocusRequester = remember { FocusRequester() }
    val ageFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }
    val dernierAyaaFocusRequester = remember { FocusRequester() }
    val mokarrareAyaaFocusRequester = remember { FocusRequester() }
    val moulahadaFocusRequester = remember { FocusRequester() }
    val tikrareFocusRequester = remember { FocusRequester() }
    val tikrar3ardFocusRequester = remember { FocusRequester() }
    val positionFocusRequester = remember { FocusRequester() }
    val questionOuiNonFocusRequester = remember { FocusRequester() }
    val absencesFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingNom) {
        if (isEditingNom) {
            nomInput = etudiant.nom
            nomFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingPrenom) {
        if (isEditingPrenom) {
            prenomInput = etudiant.prenom
            prenomFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingAge) {
        if (isEditingAge) {
            ageInput = ""
            ageFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingPhone) {
        if (isEditingPhone) {
            phoneInput = ""
            phoneFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingDernierAyaa) {
        if (isEditingDernierAyaa) {
            dernierAyaaInput = ""
            dernierAyaaFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingMokarrareAyaa) {
        if (isEditingMokarrareAyaa) {
            mokarrareAyaaInput = ""
            mokarrareAyaaFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingMoulahada) {
        if (isEditingMoulahada) {
            moulahadaInput = etudiant.moulahada_makouba
            moulahadaFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingTikrare) {
        if (isEditingTikrare) {
            tikrareInput = ""
            tikrareFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingTikrar3ard) {
        if (isEditingTikrar3ard) {
            tikrar3ardInput = ""
            tikrar3ardFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingPosition) {
        if (isEditingPosition) {
            positionInput = ""
            positionFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingQuestionOuiNon) {
        if (isEditingQuestionOuiNon) {
            questionOuiNonInput = etudiant.question_par_non
            questionOuiNonFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditingAbsences) {
        if (isEditingAbsences) {
            absencesInput = ""
            absencesFocusRequester.requestFocus()
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("تأكيد الحذف") },
            text = {
                Text("هل أنت متأكد من حذف الطالب ${etudiant.nom} ${etudiant.prenom}؟ لا يمكن التراجع عن هذا الإجراء.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        repo19Etudiant.delete(etudiant)
                        showDeleteConfirmation = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (wasUpdatedToday) {
                    Color(0xFFFFFDE7) // Light yellow
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header with delete button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${etudiant.nom} ${etudiant.prenom}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        // Show indicator if updated today
                        if (wasUpdatedToday) {
                            Text(
                                text = "✅ محدث اليوم",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF558B2F) // Dark green
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showDeleteConfirmation = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حذف")
                    }
                }

                Divider()

                // Absences sans justification (editable with print toggle)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "الغياب بدون مبرر:", style = MaterialTheme.typography.bodyMedium)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Number field
                        if (isEditingAbsences) {
                            OutlinedTextField(
                                value = absencesInput,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                                        absencesInput = newValue
                                    }
                                },
                                modifier = Modifier.width(80.dp).focusRequester(absencesFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    val newAbsences = absencesInput.toIntOrNull() ?: etudiant.nmbr_absence_sans_justification
                                    repo19Etudiant.upsert(etudiant.copy(nmbr_absence_sans_justification = newAbsences))
                                    isEditingAbsences = false
                                }),
                                singleLine = true
                            )
                        } else {
                            Text(
                                text = etudiant.nmbr_absence_sans_justification.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (etudiant.nmbr_absence_sans_justification > 0) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.clickable { isEditingAbsences = true }
                            )
                        }

                        // Print toggle button
                        IconButton(
                            onClick = {
                                repo19Etudiant.upsert(
                                    etudiant.copy(imprime_justification = !etudiant.imprime_justification)
                                )
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "طباعة المبرر",
                                tint = if (etudiant.imprime_justification) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                }
                            )
                        }
                    }
                }

                Divider()

                // Question Oui/Non (editable)
                EditableField(
                    label = "سؤال نعم/لا:",
                    value = etudiant.question_par_non,
                    isEditing = isEditingQuestionOuiNon,
                    inputValue = questionOuiNonInput,
                    onInputChange = { questionOuiNonInput = it },
                    onEditClick = { isEditingQuestionOuiNon = true },
                    onSave = {
                        repo19Etudiant.upsert(etudiant.copy(question_par_non = questionOuiNonInput))
                        isEditingQuestionOuiNon = false
                    },
                    focusRequester = questionOuiNonFocusRequester,
                    textStyle = MaterialTheme.typography.bodySmall,
                    width = 150.dp
                )

                Divider()

                // Nom (editable)
                EditableField(
                    label = "الاسم:",
                    value = etudiant.nom,
                    isEditing = isEditingNom,
                    inputValue = nomInput,
                    onInputChange = { nomInput = it },
                    onEditClick = { isEditingNom = true },
                    onSave = {
                        repo19Etudiant.upsert(etudiant.copy(nom = nomInput))
                        isEditingNom = false
                    },
                    focusRequester = nomFocusRequester
                )

                // Prenom (editable)
                EditableField(
                    label = "اللقب:",
                    value = etudiant.prenom,
                    isEditing = isEditingPrenom,
                    inputValue = prenomInput,
                    onInputChange = { prenomInput = it },
                    onEditClick = { isEditingPrenom = true },
                    onSave = {
                        repo19Etudiant.upsert(etudiant.copy(prenom = prenomInput))
                        isEditingPrenom = false
                    },
                    focusRequester = prenomFocusRequester
                )

                // Position in class (editable)
                EditableNumberField(
                    label = "المركز في الصف:",
                    value = etudiant.positon_don_classe.toString(),
                    isEditing = isEditingPosition,
                    inputValue = positionInput,
                    onInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            positionInput = newValue
                        }
                    },
                    onEditClick = { isEditingPosition = true },
                    onSave = {
                        val newPosition = positionInput.toIntOrNull() ?: etudiant.positon_don_classe
                        repo19Etudiant.upsert(etudiant.copy(positon_don_classe = newPosition))
                        isEditingPosition = false
                    },
                    focusRequester = positionFocusRequester
                )

                // Age (editable)
                EditableNumberField(
                    label = "العمر:",
                    value = etudiant.age.toString(),
                    isEditing = isEditingAge,
                    inputValue = ageInput,
                    onInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            ageInput = newValue
                        }
                    },
                    onEditClick = { isEditingAge = true },
                    onSave = {
                        val newAge = ageInput.toIntOrNull() ?: etudiant.age
                        repo19Etudiant.upsert(etudiant.copy(age = newAge))
                        isEditingAge = false
                    },
                    focusRequester = ageFocusRequester
                )

                // Phone (editable)
                EditableField(
                    label = "هاتف:",
                    value = etudiant.num_telephone_parent,
                    isEditing = isEditingPhone,
                    inputValue = phoneInput,
                    onInputChange = { phoneInput = it },
                    onEditClick = { isEditingPhone = true },
                    onSave = {
                        repo19Etudiant.upsert(etudiant.copy(num_telephone_parent = phoneInput))
                        isEditingPhone = false
                    },
                    focusRequester = phoneFocusRequester,
                    keyboardType = KeyboardType.Phone,
                    textStyle = MaterialTheme.typography.bodySmall,
                    width = 120.dp
                )

                Divider()

                // Dernier Soura
                ClickableFieldWithIcon(
                    label = "آخر سورة:",
                    value = etudiant.dernier_Soura_Wassale_Laha.arabicName,
                    onClick = onShowSouraDialog,
                    color = MaterialTheme.colorScheme.primary
                )

                // Dernier Ayaa (editable)
                EditableNumberField(
                    label = "رقم الآية:",
                    value = etudiant.dernier_Soura_sater.toString(),
                    isEditing = isEditingDernierAyaa,
                    inputValue = dernierAyaaInput,
                    onInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            dernierAyaaInput = newValue
                        }
                    },
                    onEditClick = { isEditingDernierAyaa = true },
                    onSave = {
                        val newAyaa = dernierAyaaInput.toIntOrNull() ?: etudiant.dernier_Soura_sater
                        repo19Etudiant.upsert(etudiant.copy(dernier_Soura_sater = newAyaa))
                        isEditingDernierAyaa = false
                    },
                    focusRequester = dernierAyaaFocusRequester
                )

                // Dernier Takiyim Ijtihad
                ClickableFieldWithIcon(
                    label = "تقييم الاجتهاد:",
                    value = etudiant.dernier_takyim_dabte.arabicName,
                    onClick = onShowTakiyimDialog,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Divider()

                // Mokarrare Hifde
                ClickableFieldWithIcon(
                    label = "مكررة:",
                    value = etudiant.mokarrare_hifde.arabicName,
                    onClick = onShowMokarrareDialog,
                    color = MaterialTheme.colorScheme.secondary
                )

                // Mokarrare Ayaa (editable)
                EditableNumberField(
                    label = "رقم الآية:",
                    value = etudiant.mokarrare_hifde_sater.toString(),
                    isEditing = isEditingMokarrareAyaa,
                    inputValue = mokarrareAyaaInput,
                    onInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            mokarrareAyaaInput = newValue
                        }
                    },
                    onEditClick = { isEditingMokarrareAyaa = true },
                    onSave = {
                        val newAyaa = mokarrareAyaaInput.toIntOrNull() ?: etudiant.mokarrare_hifde_sater
                        repo19Etudiant.upsert(etudiant.copy(mokarrare_hifde_sater = newAyaa))
                        isEditingMokarrareAyaa = false
                    },
                    focusRequester = mokarrareAyaaFocusRequester
                )

                Divider()

                // Tikrare (editable)
                EditableNumberField(
                    label = "تكرار:",
                    value = etudiant.tikrare.toString(),
                    isEditing = isEditingTikrare,
                    inputValue = tikrareInput,
                    onInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            tikrareInput = newValue
                        }
                    },
                    onEditClick = { isEditingTikrare = true },
                    onSave = {
                        val newTikrare = tikrareInput.toIntOrNull() ?: etudiant.tikrare
                        repo19Etudiant.upsert(etudiant.copy(tikrare = newTikrare))
                        isEditingTikrare = false
                    },
                    focusRequester = tikrareFocusRequester
                )

                // Tikrar 3ard (editable)
                EditableNumberField(
                    label = "تكرار عرض:",
                    value = etudiant.tikrare_3arde.toString(),
                    isEditing = isEditingTikrar3ard,
                    inputValue = tikrar3ardInput,
                    onInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            tikrar3ardInput = newValue
                        }
                    },
                    onEditClick = { isEditingTikrar3ard = true },
                    onSave = {
                        val newTikrar3ard = tikrar3ardInput.toIntOrNull() ?: etudiant.tikrare_3arde
                        repo19Etudiant.upsert(etudiant.copy(tikrare_3arde = newTikrar3ard))
                        isEditingTikrar3ard = false
                    },
                    focusRequester = tikrar3ardFocusRequester
                )

                Divider()

                // Section for Istedrak Kadim (Old Records)
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

                Divider()

                // Moulahada 3ala Soulouk
                ClickableFieldWithIcon(
                    label = "ملاحظة على السلوك:",
                    value = etudiant.moulahada_3ala_soulouk.arabicName,
                    onClick = onShowMoulahada3alaSouloukDialog,
                    color = MaterialTheme.colorScheme.tertiary
                )

                // Moulahada Makouba (editable)
                EditableField(
                    label = "ملاحظة مكتوبة:",
                    value = etudiant.moulahada_makouba,
                    isEditing = isEditingMoulahada,
                    inputValue = moulahadaInput,
                    onInputChange = { moulahadaInput = it },
                    onEditClick = { isEditingMoulahada = true },
                    onSave = {
                        repo19Etudiant.upsert(etudiant.copy(moulahada_makouba = moulahadaInput))
                        isEditingMoulahada = false
                    },
                    focusRequester = moulahadaFocusRequester,
                    textStyle = MaterialTheme.typography.bodySmall,
                    width = 150.dp
                )

                Text(
                    text = "Créé: ${formatDate(etudiant.creationTimestamps)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EditableField(
    label: String,
    value: String,
    isEditing: Boolean,
    inputValue: String,
    onInputChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onSave: () -> Unit,
    focusRequester: FocusRequester,
    keyboardType: KeyboardType = KeyboardType.Text,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    width: androidx.compose.ui.unit.Dp = 150.dp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        if (isEditing) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = onInputChange,
                modifier = Modifier.width(width).focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onSave() }),
                singleLine = true
            )
        } else {
            Text(
                text = value.ifBlank { "---" },
                style = textStyle,
                modifier = Modifier.clickable { onEditClick() }
            )
        }
    }
}

@Composable
private fun EditableNumberField(
    label: String,
    value: String,
    isEditing: Boolean,
    inputValue: String,
    onInputChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onSave: () -> Unit,
    focusRequester: FocusRequester
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        if (isEditing) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = onInputChange,
                modifier = Modifier.width(80.dp).focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onSave() }),
                singleLine = true
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onEditClick() }
            )
        }
    }
}

@Composable
private fun ClickableFieldWithIcon(
    label: String,
    value: String,
    onClick: () -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Change",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
