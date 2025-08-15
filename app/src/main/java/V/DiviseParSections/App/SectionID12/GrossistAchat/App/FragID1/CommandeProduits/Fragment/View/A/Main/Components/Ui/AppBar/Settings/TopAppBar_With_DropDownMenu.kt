package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.AppBar.Settings

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopAppBar_With_DropDownMenu(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    aCentralFacade: ACentralFacade= koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repo10OperationVentCouleur: Repo10OperationVentCouleur = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur,
    uiState: GrossistAchatSec12FragID1_ViewModel.UiState
) {
    val repositorysMainGetter = viewModel.aCentralFacade.repositorysMainGetter
    val data = repositorysMainGetter.repo10OperationVentCouleur.datasValue

    TopAppBar(
        modifier = Modifier
            .getSemanticsTag(
                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode,
                "currentActiveFocuced_M14VentPeriode"
            )
            .getSemanticsTag(
                data.map { it.parent_M14VentPeriod_KeyId },
                "repo10OperationVentCouleur"
            )
            .height(30.dp),
        title = {
            Text(
                "Grossist Achat",
                fontSize = 14.sp
            )
        },
        actions = {
            IconButton(onClick = { viewModel.updateShowMenu(!uiState.showMenu) }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }

            val filtered_ListM10Vent_BY_Curr_M14VentPeriod = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                .filtered_ListM10Vent_BY_Curr_M14VentPeriod

            val achats_Depuit_M11AchatOperation_List =
                viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation
                    .genere_Achats_Depuit_M11AchatOperation_List(
                        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                            .currentActiveFocuced_M14VentPeriode,
                        filtered_ListM10Vent_BY_Curr_M14VentPeriod,
                        produits = viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue
                    )

            DropdownMenu(
                modifier = Modifier
                    .getSemanticsTag(
                        data = filtered_ListM10Vent_BY_Curr_M14VentPeriod,
                        nomVal = "vents"
                    )
                    .getSemanticsTag(
                        data = filtered_ListM10Vent_BY_Curr_M14VentPeriod,
                        nomVal = "vents"
                    )
                    .getSemanticsTag(
                        achats_Depuit_M11AchatOperation_List,
                        "achats_Depuit_M11AchatOperation_List"
                    ),
                expanded = uiState.showMenu,
                onDismissRequest = { viewModel.updateShowMenu(false) }
            ) {
                DropDownItem_4(viewModel,"Dialog filter Vent Period")
                DropDownItem_3(viewModel)
                DropDownItem_2(viewModel)

                ClearFilterButton(viewModel)

                Repo11AchatOperation_deleteMulti_WithExpressiveButton(viewModel)


                val datas_repo10OperationVentCouleur= repo10OperationVentCouleur.datasValue
                val currentActiveFocuced_M14VentPeriode= focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
                Card(
                    modifier = Modifier
                        .getSemanticsTag(
                            datas_repo10OperationVentCouleur.map { it.parent_M14VentPeriod_KeyId },
                            "map_datas_repo10OperationVentCouleu"
                        )
                        .getSemanticsTag(
                            datas_repo10OperationVentCouleur,
                            "datas_repo10OperationVentCouleur"
                        )
                        .getSemanticsTag(
                            currentActiveFocuced_M14VentPeriode,
                            "currentActiveFocuced_M14VentPeriode"
                        )
                        .getSemanticsTag(
                            filtered_ListM10Vent_BY_Curr_M14VentPeriod,
                            "filtered_ListM10Vent_BY_Curr_M14VentPeriod"
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    DropdownMenuItem(
                        modifier = Modifier
                           ,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        text = { Text("genere_Achats_Depuit_M11AchatOperation_List()") },
                        onClick = {
                            achats_Depuit_M11AchatOperation_List.map {
                                viewModel.aCentralFacade.repositorysMainSetter.repo11AchatOperation_add_New(
                                    it
                                )
                            }
                            viewModel.updateShowMenu(false)
                        }
                    )
                }

                Card(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        text = { Text("Filtrer par Client") },
                        onClick = {
                            viewModel.updateShowDialog(true)
                            viewModel.updateShowMenu(false)
                        }
                    )
                }
            }
        }
    )
}
@Composable
private fun ClearFilterButton(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
) {
    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Filter",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            },
            text = { Text("Effacer le filtre") },
            onClick = {
                // Clear all filters using FocusedValuesGetter
                focusedValuesGetter.clearAllFilters()
                viewModel.updateShowMenu(false)
            }
        )
    }
}

// NEW: Material Expressive Delete Button Component
@Composable
private fun Repo11AchatOperation_deleteMulti_WithExpressiveButton(viewModel: GrossistAchatSec12FragID1_ViewModel) {
    var isDeletePressed by remember { mutableStateOf(false) }
    var isDeleteYellow by remember { mutableStateOf(false) }
    var deleteProgress by remember { mutableStateOf(0f) }

    // Animation pour le progress du bouton Delete
    val animatedProgress by animateFloatAsState(
        targetValue = if (isDeletePressed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        finishedListener = { progress ->
            if (progress == 1f && isDeletePressed) {
                // Execute delete operation after 1 second
                val keyID = viewModel.aCentralFacade
                    .focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode
                    ?.keyID

                keyID?.let { nonNullKeyID ->
                    viewModel.aCentralFacade.repositorysMainSetter.repo11AchatOperation_deleteMulti(
                        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation
                            .datasValue.filter {
                                it.parent_M14VentPeriod_KeyID == nonNullKeyID
                            }
                    )
                }

                viewModel.updateShowMenu(false)
                isDeletePressed = false
                isDeleteYellow = false
            }
        }
    )

    LaunchedEffect(animatedProgress) {
        deleteProgress = animatedProgress
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                ExpressiveDeleteIcon(
                    isPressed = isDeletePressed,
                    isYellow = isDeleteYellow,
                    progress = deleteProgress,
                    onClick = {
                        // Premier clic : active l'état jaune
                        if (!isDeleteYellow) {
                            isDeleteYellow = true
                        }
                    },
                    onLongPress = {
                        // Commence l'animation seulement si le bouton est jaune
                        if (isDeleteYellow) {
                            isDeletePressed = true
                        }
                    },
                    onRelease = {
                        if (deleteProgress < 1f) {
                            isDeletePressed = false
                        }
                    }
                )
            },
            text = {
                Text(
                    "Supprimer par Période",
                    color = when {
                        isDeleteYellow -> Color(0xFFF59E0B)
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            },
            onClick = {
                // Premier clic : active l'état jaune
                if (!isDeleteYellow) {
                    isDeleteYellow = true
                }
            }
        )
    }
}

@Composable
private fun ExpressiveDeleteIcon(
    isPressed: Boolean = false,
    isYellow: Boolean = false,
    progress: Float = 0f,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {},
    onRelease: () -> Unit = {}
) {
    // Animation de scale
    val scale by animateFloatAsState(
        targetValue = if (isPressed || isYellow) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .size(24.dp)
            .scale(scale)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onLongPress()
                        tryAwaitRelease()
                        onRelease()
                    },
                    onTap = {
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Fond du bouton
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = when {
                        isYellow -> Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFBBF24),
                                Color(0xFFF59E0B)
                            )
                        )

                        else -> Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    },
                    shape = CircleShape
                )
        )

        // Indicateur de progression pour le bouton Delete
        if (progress > 0f) {
            // Cercle de progression
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val strokeWidth = 2.dp.toPx()
                drawArc(
                    color = Color(0xFFDC2626), // Rouge pour delete
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    ),
                    size = Size(
                        size.width - strokeWidth,
                        size.height - strokeWidth
                    ),
                    topLeft = Offset(
                        strokeWidth / 2,
                        strokeWidth / 2
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = when {
                isYellow -> Color.White
                else -> MaterialTheme.colorScheme.error
            },
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun DropDownItem_3(viewModel: GrossistAchatSec12FragID1_ViewModel) {
    val text = "Choisir Client"
    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            text = { Text(text) },
            onClick = {
                viewModel.update_show_Dialog_filter_AChats_Par_Client_Acheteur(true)
            }
        )
    }
}

@Composable
private fun DropDownItem_4(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    text: String,
    focusedValuesGetter: FocusedValuesGetter= koinInject()
) {
    
    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(value = focusedValuesGetter.active_Central_Values.active_M14VentPeriode_AuFilterAchats, key = SemanticsPropertyKey(""))
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            text = { Text(text) },
            onClick = {
                viewModel.update_dialog_Filter_VentPeriod_showDialog(true)
            }
        )
    }
}

@Composable
private fun DropDownItem_2(viewModel: GrossistAchatSec12FragID1_ViewModel) {
    val text = "Choisir Grossiste"
    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            text = { Text(text) },
            onClick = {
                viewModel.update_dialog_Choisire_Grossist_Modularized_showDialog(pour_MainScreen = true)
                viewModel.updateShowMenu(false)
            }
        )
    }
}
