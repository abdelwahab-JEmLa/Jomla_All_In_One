package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main

import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.MonthSelectionDialog
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.ViewModel.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog.SessionsEducationDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientjetpack.R
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun A_EducationFragment(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    onNavigateBack: (() -> Unit)? = null,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    val context = LocalContext.current
    val appDatabase = koinInject<AppDatabase>()

    val params = aCentralFacade.repositorysMainGetter.repo18CentralParametresOfAllApps.dataValue
    val currentComptKeyId = params?.au_Lence_Set_Compt_Ac_KeyId ?: ""
    val currentUtilisateur = M00CentralParametresOfAllApps.get_utilisateur(currentComptKeyId)

    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val searchQuery = activeCentralValues.outlined_filter_searcher_floating_abouve_all

    var isSearchActive by remember { mutableStateOf(searchQuery.isNotEmpty()) }
    var selectedEtudiantForSessions by remember { mutableStateOf<M19Etudiant?>(null) }

    val activeOusstad = activeCentralValues.active_Ousstad_Tahfid
    val ousstadTitle = activeOusstad?.nom_arab ?: "قسم حفظة القرآن منظم"

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

    // FIXED: Apply filter based on activeOusstad from activeCentralValues
    LaunchedEffect(activeOusstad) {
        repo19Etudiant.setFilter(activeOusstad)
    }

    // FIXED: Use filtered_datasValue instead of switching based on currentUtilisateur
    // The filter is now properly set by activeOusstad above
    val baseEtudiants = repo19Etudiant.filtered_datasValue

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

    val hasUpdateToday = remember(etudiants) {
        etudiants.any { etudiant ->
            val updateTimestamp = etudiant.dernierTimeTampsSynchronisationAvecFireBase
            isSameDay(updateTimestamp, System.currentTimeMillis())
        }
    }

    val totalStudents = etudiants.size
    val updatedToday = etudiants.count { etudiant ->
        val updateTimestamp = etudiant.dernierTimeTampsSynchronisationAvecFireBase ?: etudiant.creationTimestamps
        isSameDay(updateTimestamp, System.currentTimeMillis())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        // Auto-scrolling banner
        ScrollableInformationBanner(
            ousstadName = activeOusstad?.nom_arab ?: "",
            modifier = Modifier.fillMaxWidth()
        )

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
                ) { etudiant ->
                    EtudiantCard(
                        etudiant = etudiant,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
fun ScrollableInformationBanner(
    ousstadName: String,
    modifier: Modifier = Modifier
) {
    var currentBannerIndex by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val cardWidth = with(density) { 320.dp.toPx() }
    val totalCards = 3

    // Auto-scroll behavior similar to ScrolleAdBanner
    LaunchedEffect(Unit) {
        while (true) {
            // Forward scroll (left to right)
            while (currentBannerIndex < totalCards - 1) {
                delay(1500)

                val totalSteps = 35
                val stepSize = cardWidth / totalSteps

                for (step in 0 until totalSteps) {
                    val nextPosition = (currentBannerIndex * cardWidth) + (step * stepSize)
                    scrollState.scrollTo(nextPosition.toInt())
                    delay(10)
                }

                currentBannerIndex++
            }

            delay(3000)

            // Reverse scroll (right to left)
            val totalSteps = 35
            val maxScroll = (totalCards - 1) * cardWidth
            val stepSize = maxScroll / totalSteps

            for (step in 0 until totalSteps) {
                val nextPosition = maxScroll - (step * stepSize)
                scrollState.scrollTo(nextPosition.toInt())
                delay(10)
            }

            currentBannerIndex = 0
        }
    }

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner 1: School logo and blessing
        Card(
            modifier = Modifier
                .width(320.dp)
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ecole_logo1),
                    contentDescription = "School Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )

             /*   Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "وفقنا الله وإياكم لما يحب ويرضى",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }           */
            }
        }

        // Banner 2: Ousstad name
        Card(
            modifier = Modifier
                .width(320.dp)
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "الأستاذ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = ousstadName.ifEmpty { "قسم حفظة القرآن" },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Banner 3: Goals
        Card(
            modifier = Modifier
                .width(320.dp)
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "الأهداف المبتغاة",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "إيصال الغيابات والتقارير للإدارة\nتسهيل تذكر المحفوظ للطلبة",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
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
