package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Ousstad_Tahfid
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Utilisateur
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.MonthSelectionDialog
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationFragment(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    onNavigateBack: (() -> Unit)? = null,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    val params = aCentralFacade.repositorysMainGetter.repo18CentralParametresOfAllApps.dataValue
    val currentComptKeyId = params?.au_Lence_Set_Compt_Ac_KeyId ?: ""
    val currentUtilisateur = M18CentralParametresOfAllApps.get_utilisateur(currentComptKeyId)

    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val searchQuery = activeCentralValues.outlined_filter_searcher_floating_abouve_all

    var isSearchActive by remember { mutableStateOf(searchQuery.isNotEmpty()) }

    // Track selected student locally
    var selectedEtudiantForSessions by remember { mutableStateOf<M19Etudiant?>(null) }

    // Show month selection dialog when needed
    if (activeCentralValues.displaye_dialog_mois_moinAcPlus_6_du_current) {
        MonthSelectionDialog(
            onDismiss = {
                focusedValuesGetter.update_activeCentralValues(
                    activeCentralValues.copy(
                        displaye_dialog_mois_moinAcPlus_6_du_current = false
                    )
                )
                selectedEtudiantForSessions = null
            },
            onMonthSelected = { selectedMonth ->
                focusedValuesGetter.update_activeCentralValues(
                    activeCentralValues.copy(
                        displaye_dialog_mois_moinAcPlus_6_du_current = false,
                        displaye_sections_education_du_mois = selectedMonth
                    )
                )
            }
        )
    }

    // Show sessions education dialog when month is selected
    val selectedMonth = activeCentralValues.displaye_sections_education_du_mois
    if (selectedMonth != null) {
        val repo20Observation = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion

        SessionsEducationDialog(
            selectedMonth = selectedMonth,
            repo20Observation = repo20Observation,
            onDismiss = {
                focusedValuesGetter.update_activeCentralValues(
                    activeCentralValues.copy(
                        displaye_sections_education_du_mois = null
                    )
                )
                selectedEtudiantForSessions = null
            }
        )
    }

    // Map Utilisateur to Ousstad_Tahfid before setting filter
    LaunchedEffect(currentUtilisateur) {
        val ousstad = mapUtilisateurToOusstad(currentUtilisateur)
        repo19Etudiant.setFilter(ousstad)
    }

    // Use filtered data if user is Amine_Madrassa, otherwise use all data
    val baseEtudiants = if (currentUtilisateur == Utilisateur.Amine_Madrassa) {
        repo19Etudiant.filtered_datasValue
    } else {
        repo19Etudiant.datasValue
    }

    // Apply name filter when search query is not blank
    val etudiants = if (searchQuery.isNotBlank()) {
        baseEtudiants.filter { etudiant ->
            etudiant.nom.contains(searchQuery, ignoreCase = true) ||
                    etudiant.prenom.contains(searchQuery, ignoreCase = true)
        }
    } else {
        baseEtudiants
    }.sortedWith(
        compareByDescending<M19Etudiant> { it.dernierTimeTampsSynchronisationAvecFireBase }
            .thenBy { it.positon_don_classe }
    )

    // Check if any student was updated today
    val hasUpdateToday = remember(etudiants) {
        etudiants.any { etudiant ->
            val updateTimestamp = etudiant.dernierTimeTampsSynchronisationAvecFireBase
            isSameDay(updateTimestamp, System.currentTimeMillis())
        }
    }

    // Calculate statistics
    val totalStudents = etudiants.size
    val updatedToday = etudiants.count { etudiant ->
        val updateTimestamp = etudiant.dernierTimeTampsSynchronisationAvecFireBase ?: etudiant.creationTimestamps
        isSameDay(updateTimestamp, System.currentTimeMillis())
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "قسم حفظة القرآن منظم",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = "حمنيش عبدالوهاب",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = if (hasUpdateToday) {
                                    Color(0xFFFFC107)
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {

            if (etudiants.isEmpty()) {
                EmptyState(
                    modifier = Modifier.fillMaxSize(),
                    isFiltered = isSearchActive && searchQuery.isNotBlank()
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = etudiants,
                        key = { etudiant -> etudiant.keyID }
                    ) { etudiant ->
                        EtudiantCard(
                            etudiant = etudiant,
                            modifier = Modifier.fillMaxWidth(),
                            focusedValuesGetter = focusedValuesGetter,
                            onEtudiantSelected = { }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Maps Utilisateur enum to Ousstad_Tahfid enum
 * Returns null for users who don't have a corresponding Ousstad role
 */
private fun mapUtilisateurToOusstad(utilisateur: Utilisateur): Ousstad_Tahfid? {
    return when (utilisateur) {
        Utilisateur.Amine_Madrassa -> Ousstad_Tahfid.Amine_Madrassa
        Utilisateur.Abdelwahab_Osstad -> Ousstad_Tahfid.Abdelwahab_Osstad
        else -> null // For other users, no filter is applied
    }
}

@Composable
fun EtudiantCard(
    etudiant: M19Etudiant,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter,
    onEtudiantSelected: (M19Etudiant) -> Unit
) {
    val activeCentralValues = focusedValuesGetter.active_Central_Values

    Card(
        modifier = modifier
            .clickable {
                // Show month selection dialog directly
                focusedValuesGetter.update_activeCentralValues(
                    activeCentralValues.copy(
                        displaye_dialog_mois_moinAcPlus_6_du_current = true
                    )
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Student icon or avatar
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Student name
            Text(
                text = "${etudiant.nom} ${etudiant.prenom}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                minLines = 2
            )

            // Position in class
            Text(
                text = "المرتبة: ${etudiant.positon_don_classe}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    isFiltered: Boolean = false
) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isFiltered) Icons.Default.Search else Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isFiltered) "لا توجد نتائج" else "لا يوجد طلاب مسجلين",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (isFiltered) "جرب البحث بكلمات أخرى" else "سيظهر الطلاب هنا بعد إضافتهم",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
