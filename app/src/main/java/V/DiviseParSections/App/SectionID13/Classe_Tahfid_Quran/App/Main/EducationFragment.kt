package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SOUAR
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationFragment(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    onNavigateBack: (() -> Unit)? = null
) {
    val etudiants = repo19Etudiant.datasValue

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Classe Tahfid Quran",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (etudiants.isEmpty()) {
            EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = etudiants,
                    key = { index, _ -> index }
                ) { index, etudiant ->
                    EtudiantCard(
                        etudiant = etudiant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aucun étudiant enregistré",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Les étudiants apparaîtront ici une fois ajoutés",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EtudiantCard(
    etudiant: M19Etudiant,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showSouraDialog by remember { mutableStateOf(false) }
    var showMokarrareDialog by remember { mutableStateOf(false) }
    var isEditingAge by remember { mutableStateOf(false) }
    var isEditingPhone by remember { mutableStateOf(false) }
    var isEditingDernierAyaa by remember { mutableStateOf(false) }
    var isEditingMokarrareAyaa by remember { mutableStateOf(false) }

    var ageInput by remember { mutableStateOf(etudiant.age.toString()) }
    var phoneInput by remember { mutableStateOf(etudiant.num_telephone_parent) }
    var dernierAyaaInput by remember { mutableStateOf(etudiant.dernier_Soura_num_Ayaa.toString()) }
    var mokarrareAyaaInput by remember { mutableStateOf(etudiant.mokarrare_hifde_num_Ayaa.toString()) }

    val ageFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }
    val dernierAyaaFocusRequester = remember { FocusRequester() }
    val mokarrareAyaaFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingAge) {
        if (isEditingAge) ageFocusRequester.requestFocus()
    }
    LaunchedEffect(isEditingPhone) {
        if (isEditingPhone) phoneFocusRequester.requestFocus()
    }
    LaunchedEffect(isEditingDernierAyaa) {
        if (isEditingDernierAyaa) dernierAyaaFocusRequester.requestFocus()
    }
    LaunchedEffect(isEditingMokarrareAyaa) {
        if (isEditingMokarrareAyaa) mokarrareAyaaFocusRequester.requestFocus()
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Collapsed view - Minimal info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
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

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Expanded view - Full details in single column
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Divider()

                    // Age (editable)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "العمر:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (isEditingAge) {
                            OutlinedTextField(
                                value = ageInput,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                                        ageInput = newValue
                                    }
                                },
                                modifier = Modifier
                                    .width(80.dp)
                                    .focusRequester(ageFocusRequester),
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
                        Text(
                            text = "هاتف:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (isEditingPhone) {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                modifier = Modifier
                                    .width(120.dp)
                                    .focusRequester(phoneFocusRequester),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSouraDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "آخر سورة:",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                        Text(
                            text = "رقم الآية:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (isEditingDernierAyaa) {
                            OutlinedTextField(
                                value = dernierAyaaInput,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                                        dernierAyaaInput = newValue
                                    }
                                },
                                modifier = Modifier
                                    .width(80.dp)
                                    .focusRequester(dernierAyaaFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        val newAyaa = dernierAyaaInput.toIntOrNull() ?: etudiant.dernier_Soura_num_Ayaa
                                        repo19Etudiant.upsert(etudiant.copy(dernier_Soura_num_Ayaa = newAyaa))
                                        isEditingDernierAyaa = false
                                    }
                                ),
                                singleLine = true
                            )
                        } else {
                            Text(
                                text = etudiant.dernier_Soura_num_Ayaa.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable { isEditingDernierAyaa = true }
                            )
                        }
                    }

                    Divider()

                    // Mokarrare Hifde
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMokarrareDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مكررة:",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                        Text(
                            text = "رقم الآية:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (isEditingMokarrareAyaa) {
                            OutlinedTextField(
                                value = mokarrareAyaaInput,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull() != null)) {
                                        mokarrareAyaaInput = newValue
                                    }
                                },
                                modifier = Modifier
                                    .width(80.dp)
                                    .focusRequester(mokarrareAyaaFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        val newAyaa = mokarrareAyaaInput.toIntOrNull() ?: etudiant.mokarrare_hifde_num_Ayaa
                                        repo19Etudiant.upsert(etudiant.copy(mokarrare_hifde_num_Ayaa = newAyaa))
                                        isEditingMokarrareAyaa = false
                                    }
                                ),
                                singleLine = true
                            )
                        } else {
                            Text(
                                text = etudiant.mokarrare_hifde_num_Ayaa.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable { isEditingMokarrareAyaa = true }
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

    // Dialogs
    if (showSouraDialog) {
        SouraSelectionDialog(
            currentSoura = etudiant.dernier_Soura_Wassale_Laha,
            onDismiss = { showSouraDialog = false },
            onSelect = { selectedSoura ->
                repo19Etudiant.upsert(etudiant.copy(dernier_Soura_Wassale_Laha = selectedSoura))
                showSouraDialog = false
            }
        )
    }

    if (showMokarrareDialog) {
        SouraSelectionDialog(
            currentSoura = etudiant.mokarrare_hifde,
            onDismiss = { showMokarrareDialog = false },
            onSelect = { selectedSoura ->
                repo19Etudiant.upsert(etudiant.copy(mokarrare_hifde = selectedSoura))
                showMokarrareDialog = false
            }
        )
    }
}

@Composable
private fun SouraSelectionDialog(
    currentSoura: SOUAR,
    onDismiss: () -> Unit,
    onSelect: (SOUAR) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "اختر السورة",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SOUAR.entries.size) { index ->
                        val soura = SOUAR.entries[index]
                        Card(
                            modifier = Modifier
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
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
