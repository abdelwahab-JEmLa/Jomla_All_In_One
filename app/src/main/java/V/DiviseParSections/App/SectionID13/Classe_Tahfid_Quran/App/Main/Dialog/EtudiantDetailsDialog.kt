package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.IstedrakSection
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Moukarar
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.TakiyimEvaluationSection
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Sections.AttendanceAndBehaviorSection
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Sections.BasicInfoSection
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Sections.Moulahadat_Kadima
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.formatDate
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import android.text.format.DateUtils.isToday
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

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

    // Edit states
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

    // Input values
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

    // Focus requesters
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

    // LaunchedEffects for focus management
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (wasUpdatedToday) {
                    Color(0xFFFFFDE7)
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
                            text = "${etudiant.nom} ${etudiant.prenom} (${etudiant.age})",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (wasUpdatedToday) {
                            Text(
                                text = "✅ محدث اليوم",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF558B2F)
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

                TakiyimEvaluationSection(
                    etudiant = etudiant,
                    onShowTakiyimDialog = onShowTakiyimDialog
                )

                Divider()

                // === MEMORIZATION PROGRAM SECTION ===
                Moukarar(
                    etudiant = etudiant,
                    onShowSouraDialog = onShowSouraDialog,
                    isEditingDernierAyaa = isEditingDernierAyaa,
                    dernierAyaaInput = dernierAyaaInput,
                    onDernierAyaaInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            dernierAyaaInput = newValue
                        }
                    },
                    onDernierAyaaEditClick = { isEditingDernierAyaa = true },
                    onDernierAyaaSave = {
                        val newAyaa = dernierAyaaInput.toIntOrNull() ?: etudiant.dernier_Soura_sater
                        repo19Etudiant.upsert(etudiant.copy(dernier_Soura_sater = newAyaa))
                        isEditingDernierAyaa = false
                    },
                    dernierAyaaFocusRequester = dernierAyaaFocusRequester,
                    isEditingMokarrareAyaa = isEditingMokarrareAyaa,
                    mokarrareAyaaInput = mokarrareAyaaInput,
                    onMokarrareAyaaInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            mokarrareAyaaInput = newValue
                        }
                    },
                    onMokarrareAyaaEditClick = { isEditingMokarrareAyaa = true },
                    onMokarrareAyaaSave = {
                        val newAyaa = mokarrareAyaaInput.toIntOrNull() ?: etudiant.mokarrare_hifde_sater
                        repo19Etudiant.upsert(etudiant.copy(mokarrare_hifde_sater = newAyaa))
                        isEditingMokarrareAyaa = false
                    },
                    mokarrareAyaaFocusRequester = mokarrareAyaaFocusRequester,
                    onShowTakiyimDialog = onShowTakiyimDialog
                )

                Divider()

                // === REVIEW SECTION ===
                Moulahadat_Kadima(
                    etudiant = etudiant,
                )
                Divider()

                // === ISTEDRAK SECTION ===
                IstedrakSection(
                    etudiant = etudiant,
                    onShowIstedrakSouraDialog = onShowIstedrakSouraDialog,
                    onShowIstedrakMokarrareDialog = onShowIstedrakMokarrareDialog,
                    onShowIstedrakTakiyimDialog = onShowIstedrakTakiyimDialog
                )
                Divider()

                // === BASIC INFO SECTION ===
                BasicInfoSection(
                    etudiant = etudiant,
                    repo19Etudiant = repo19Etudiant,
                    isEditingNom = isEditingNom,
                    nomInput = nomInput,
                    onNomInputChange = { nomInput = it },
                    onNomEditClick = { isEditingNom = true },
                    onNomSave = {
                        repo19Etudiant.upsert(etudiant.copy(nom = nomInput))
                        isEditingNom = false
                    },
                    nomFocusRequester = nomFocusRequester,
                    isEditingPrenom = isEditingPrenom,
                    prenomInput = prenomInput,
                    onPrenomInputChange = { prenomInput = it },
                    onPrenomEditClick = { isEditingPrenom = true },
                    onPrenomSave = {
                        repo19Etudiant.upsert(etudiant.copy(prenom = prenomInput))
                        isEditingPrenom = false
                    },
                    prenomFocusRequester = prenomFocusRequester,
                    isEditingAge = isEditingAge,
                    ageInput = ageInput,
                    onAgeInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            ageInput = newValue
                        }
                    },
                    onAgeEditClick = { isEditingAge = true },
                    onAgeSave = {
                        val newAge = ageInput.toIntOrNull() ?: etudiant.age
                        repo19Etudiant.upsert(etudiant.copy(age = newAge))
                        isEditingAge = false
                    },
                    ageFocusRequester = ageFocusRequester,
                    isEditingPosition = isEditingPosition,
                    positionInput = positionInput,
                    onPositionInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            positionInput = newValue
                        }
                    },
                    onPositionEditClick = { isEditingPosition = true },
                    onPositionSave = {
                        val newPosition = positionInput.toIntOrNull() ?: etudiant.positon_don_classe
                        repo19Etudiant.upsert(etudiant.copy(positon_don_classe = newPosition))
                        isEditingPosition = false
                    },
                    positionFocusRequester = positionFocusRequester,
                    isEditingPhone = isEditingPhone,
                    phoneInput = phoneInput,
                    onPhoneInputChange = { phoneInput = it },
                    onPhoneEditClick = { isEditingPhone = true },
                    onPhoneSave = {
                        repo19Etudiant.upsert(etudiant.copy(num_telephone_parent = phoneInput))
                        isEditingPhone = false
                    },
                    phoneFocusRequester = phoneFocusRequester
                )

                Divider()

                // === ATTENDANCE AND BEHAVIOR SECTION ===
                AttendanceAndBehaviorSection(
                    etudiant = etudiant,
                    repo19Etudiant = repo19Etudiant,
                    isEditingAbsences = isEditingAbsences,
                    absencesInput = absencesInput,
                    onAbsencesInputChange = { newValue ->
                        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                            absencesInput = newValue
                        }
                    },
                    onAbsencesEditClick = { isEditingAbsences = true },
                    onAbsencesSave = {
                        val newAbsences = absencesInput.toIntOrNull() ?: etudiant.nmbr_absence_sans_justification
                        repo19Etudiant.upsert(etudiant.copy(nmbr_absence_sans_justification = newAbsences))
                        isEditingAbsences = false
                    },
                    absencesFocusRequester = absencesFocusRequester,
                    isEditingQuestionOuiNon = isEditingQuestionOuiNon,
                    questionOuiNonInput = questionOuiNonInput,
                    onQuestionOuiNonInputChange = { questionOuiNonInput = it },
                    onQuestionOuiNonEditClick = { isEditingQuestionOuiNon = true },
                    onQuestionOuiNonSave = {
                        repo19Etudiant.upsert(etudiant.copy(question_par_non = questionOuiNonInput))
                        isEditingQuestionOuiNon = false
                    },
                    questionOuiNonFocusRequester = questionOuiNonFocusRequester,
                    onShowMoulahada3alaSouloukDialog = onShowMoulahada3alaSouloukDialog,
                    isEditingMoulahada = isEditingMoulahada,
                    moulahadaInput = moulahadaInput,
                    onMoulahadaInputChange = { moulahadaInput = it },
                    onMoulahadaEditClick = { isEditingMoulahada = true },
                    onMoulahadaSave = {
                        repo19Etudiant.upsert(etudiant.copy(moulahada_makouba = moulahadaInput))
                        isEditingMoulahada = false
                    },
                    moulahadaFocusRequester = moulahadaFocusRequester
                )

                Divider()

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
