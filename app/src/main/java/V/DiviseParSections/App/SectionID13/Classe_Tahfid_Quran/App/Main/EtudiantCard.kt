package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.koin.compose.koinInject

@Composable
fun EtudiantCard(
    etudiant: M19Etudiant,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    modifier: Modifier = Modifier
) {
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showSouraDialog by remember { mutableStateOf(false) }
    var showMokarrareDialog by remember { mutableStateOf(false) }
    var showTakiyimDialog by remember { mutableStateOf(false) }
    var showMoulahada3alaSouloukDialog by remember { mutableStateOf(false) }

    // Compact card - Just shows basic info
    Card(
        modifier = modifier.clickable { showDetailsDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = etudiant.nom.ifBlank { "---" },
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = etudiant.prenom.ifBlank { "---" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${etudiant.age} سنة",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    // Full details dialog
    if (showDetailsDialog) {
        EtudiantDetailsDialog(
            etudiant = etudiant,
            repo19Etudiant = repo19Etudiant,
            onDismiss = { showDetailsDialog = false },
            onShowSouraDialog = { showSouraDialog = true },
            onShowMokarrareDialog = { showMokarrareDialog = true },
            onShowTakiyimDialog = { showTakiyimDialog = true },
            onShowMoulahada3alaSouloukDialog = { showMoulahada3alaSouloukDialog = true }
        )
    }

    // Selection Dialogs
    if (showSouraDialog) {
        SouraSelectionDialog(
            currentSoura = etudiant.dernier_Soura_Wassale_Laha,
            onDismiss = { showSouraDialog = false },
            onSelect = { selectedSoura ->
                repo19Etudiant.upsert(
                    etudiant.copy(
                        mokarrare_hifde = etudiant.dernier_Soura_Wassale_Laha,
                        mokarrare_hifde_sater = etudiant.dernier_Soura_sater,
                        dernier_Soura_Wassale_Laha = selectedSoura,
                        dernier_Soura_sater = 1
                    )
                )
                showSouraDialog = false
            }
        )
    }

    if (showMokarrareDialog) {
        SouraSelectionDialog(
            currentSoura = etudiant.mokarrare_hifde,
            onDismiss = { showMokarrareDialog = false },
            onSelect = { selectedSoura ->
                repo19Etudiant.upsert(
                    etudiant.copy(
                        mokarrare_hifde = selectedSoura,
                        mokarrare_hifde_sater = 1
                    )
                )
                showMokarrareDialog = false
            }
        )
    }

    if (showTakiyimDialog) {
        TakiyimSelectionDialog(
            currentTakiyim = etudiant.dernier_takyim_dabte,
            onDismiss = { showTakiyimDialog = false },
            onSelect = { selectedTakiyim ->
                repo19Etudiant.upsert(
                    etudiant.copy(
                        dernier_takyim_dabte = selectedTakiyim,
                        dernier_Soura_Wassale_Laha = etudiant.mokarrare_hifde,
                        dernier_Soura_sater = etudiant.mokarrare_hifde_sater
                    )
                )
                showTakiyimDialog = false
            }
        )
    }

    if (showMoulahada3alaSouloukDialog) {
        TakiyimSelectionDialog(
            currentTakiyim = etudiant.moulahada_3ala_soulouk,
            onDismiss = { showMoulahada3alaSouloukDialog = false },
            onSelect = { selectedTakiyim ->
                repo19Etudiant.upsert(etudiant.copy(moulahada_3ala_soulouk = selectedTakiyim))
                showMoulahada3alaSouloukDialog = false
            }
        )
    }
}

@Composable
private fun EtudiantDetailsDialog(
    etudiant: M19Etudiant,
    repo19Etudiant: Repo19Etudiant,
    onDismiss: () -> Unit,
    onShowSouraDialog: () -> Unit,
    onShowMokarrareDialog: () -> Unit,
    onShowTakiyimDialog: () -> Unit,
    onShowMoulahada3alaSouloukDialog: () -> Unit
) {          //<--
//TODO(1):  ajou nom prenom avec edites
    var isEditingAge by remember { mutableStateOf(false) }
    var isEditingPhone by remember { mutableStateOf(false) }
    var isEditingDernierAyaa by remember { mutableStateOf(false) }
    var isEditingMokarrareAyaa by remember { mutableStateOf(false) }
    var isEditingMoulahada by remember { mutableStateOf(false) }
    var isEditingTikrare by remember { mutableStateOf(false) }
    var isEditingTikrar3ard by remember { mutableStateOf(false) }
    var isEditingPosition by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var ageInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var dernierAyaaInput by remember { mutableStateOf("") }
    var mokarrareAyaaInput by remember { mutableStateOf("") }
    var moulahadaInput by remember { mutableStateOf("") }
    var tikrareInput by remember { mutableStateOf("") }
    var tikrar3ardInput by remember { mutableStateOf("") }
    var positionInput by remember { mutableStateOf("") }

    val ageFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }
    val dernierAyaaFocusRequester = remember { FocusRequester() }
    val mokarrareAyaaFocusRequester = remember { FocusRequester() }
    val moulahadaFocusRequester = remember { FocusRequester() }
    val tikrareFocusRequester = remember { FocusRequester() }
    val tikrar3ardFocusRequester = remember { FocusRequester() }
    val positionFocusRequester = remember { FocusRequester() }

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
            modifier = Modifier.fillMaxWidth()
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
                    Text(
                        text = "${etudiant.nom} ${etudiant.prenom}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
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

                // Position in class (editable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "المركز في الصف:", style = MaterialTheme.typography.bodyMedium)
                    if (isEditingPosition) {
                        OutlinedTextField(
                            value = positionInput,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                                    positionInput = newValue
                                }
                            },
                            modifier = Modifier.width(80.dp).focusRequester(positionFocusRequester),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val newPosition = positionInput.toIntOrNull() ?: etudiant.positon_don_classe
                                    repo19Etudiant.upsert(etudiant.copy(positon_don_classe = newPosition))
                                    isEditingPosition = false
                                }
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = "${etudiant.positon_don_classe}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { isEditingPosition = true }
                        )
                    }
                }

                // Age (editable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "العمر:", style = MaterialTheme.typography.bodyMedium)
                    if (isEditingAge) {
                        OutlinedTextField(
                            value = ageInput,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                                    ageInput = newValue
                                }
                            },
                            modifier = Modifier.width(80.dp).focusRequester(ageFocusRequester),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val newAge = ageInput.toIntOrNull() ?: etudiant.age
                                    repo19Etudiant.upsert(etudiant.copy(age = newAge))
                                    isEditingAge = false
                                }
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = "${etudiant.age}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { isEditingAge = true }
                        )
                    }
                }

                // Phone (editable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "هاتف:", style = MaterialTheme.typography.bodyMedium)
                    if (isEditingPhone) {
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            modifier = Modifier.width(120.dp).focusRequester(phoneFocusRequester),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    repo19Etudiant.upsert(etudiant.copy(num_telephone_parent = phoneInput))
                                    isEditingPhone = false
                                }
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = etudiant.num_telephone_parent,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.clickable { isEditingPhone = true }
                        )
                    }
                }

                Divider()

                // Dernier Soura
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onShowSouraDialog() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "آخر سورة:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = etudiant.dernier_Soura_Wassale_Laha.arabicName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Dernier Ayaa (editable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "رقم الآية:", style = MaterialTheme.typography.bodyMedium)
                    if (isEditingDernierAyaa) {
                        OutlinedTextField(
                            value = dernierAyaaInput,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                                    dernierAyaaInput = newValue
                                }
                            },
                            modifier = Modifier.width(80.dp).focusRequester(dernierAyaaFocusRequester),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val newAyaa = dernierAyaaInput.toIntOrNull() ?: etudiant.dernier_Soura_sater
                                    repo19Etudiant.upsert(etudiant.copy(dernier_Soura_sater = newAyaa))
                                    isEditingDernierAyaa = false
                                }
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = etudiant.dernier_Soura_sater.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { isEditingDernierAyaa = true }
                        )
                    }
                }

                // Dernier Takiyim Ijtihad
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onShowTakiyimDialog() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "تقييم الاجتهاد:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = etudiant.dernier_takyim_dabte.arabicName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Divider()

                // Mokarrare Hifde
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onShowMokarrareDialog() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "مكررة:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = etudiant.mokarrare_hifde.arabicName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Mokarrare Ayaa (editable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "رقم الآية:", style = MaterialTheme.typography.bodyMedium)
                    if (isEditingMokarrareAyaa) {
                        OutlinedTextField(
                            value = mokarrareAyaaInput,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                                    mokarrareAyaaInput = newValue
                                }
                            },
                            modifier = Modifier.width(80.dp).focusRequester(mokarrareAyaaFocusRequester),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val newAyaa = mokarrareAyaaInput.toIntOrNull() ?: etudiant.mokarrare_hifde_sater
                                    repo19Etudiant.upsert(etudiant.copy(mokarrare_hifde_sater = newAyaa))
                                    isEditingMokarrareAyaa = false
                                }
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = etudiant.mokarrare_hifde_sater.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { isEditingMokarrareAyaa = true }
                        )
                    }
                }

                Divider()

                // Tikrare (editable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "تكرار:", style = MaterialTheme.typography.bodyMedium)
                    if (isEditingTikrare) {
                        OutlinedTextField(
                            value = tikrareInput,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                                    tikrareInput = newValue
                                }
                            },
                            modifier = Modifier.width(80.dp).focusRequester(tikrareFocusRequester),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val newTikrare = tikrareInput.toIntOrNull() ?: etudiant.tikrare
                                    repo19Etudiant.upsert(etudiant.copy(tikrare = newTikrare))
                                    isEditingTikrare = false
                                }
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = etudiant.tikrare.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { isEditingTikrare = true }
                        )
                    }
                }

                // Tikrar 3ard (editable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "تكرار عرض:", style = MaterialTheme.typography.bodyMedium)
                    if (isEditingTikrar3ard) {
                        OutlinedTextField(
                            value = tikrar3ardInput,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                                    tikrar3ardInput = newValue
                                }
                            },
                            modifier = Modifier.width(80.dp).focusRequester(tikrar3ardFocusRequester),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val newTikrar3ard = tikrar3ardInput.toIntOrNull() ?: etudiant.tikrare_3arde
                                    repo19Etudiant.upsert(etudiant.copy(tikrare_3arde = newTikrar3ard))
                                    isEditingTikrar3ard = false
                                }
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = etudiant.tikrare_3arde.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { isEditingTikrar3ard = true }
                        )
                    }
                }

                Divider()

                // Moulahada 3ala Soulouk
                Row(         //<--
                //TODO(1): ajout btton change au rien etate
                    modifier = Modifier.fillMaxWidth().clickable { onShowMoulahada3alaSouloukDialog() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "ملاحظة على السلوك:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = etudiant.moulahada_3ala_soulouk.arabicName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Moulahada Makouba (editable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "ملاحظة مكتوبة:", style = MaterialTheme.typography.bodyMedium)
                    if (isEditingMoulahada) {
                        OutlinedTextField(
                            value = moulahadaInput,
                            onValueChange = { moulahadaInput = it },
                            modifier = Modifier.width(150.dp).focusRequester(moulahadaFocusRequester),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    repo19Etudiant.upsert(etudiant.copy(moulahada_makouba = moulahadaInput))
                                    isEditingMoulahada = false
                                }
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = etudiant.moulahada_makouba.ifBlank { "---" },
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.clickable { isEditingMoulahada = true }
                        )
                    }
                }

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
