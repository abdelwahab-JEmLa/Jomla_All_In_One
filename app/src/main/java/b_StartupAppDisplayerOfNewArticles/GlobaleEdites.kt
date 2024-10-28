package b_StartupAppDisplayerOfNewArticles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// FloatingActionButtons.kt
@Composable                       //TODO utilise les comments
//poure reorganize ce floating au comments n oublie pas le scrolle et hide on buttons
///
fun FloatingActionButtonGroup(
    onToggleNavBar: () -> Unit,
    onToggleOutlineFilter: () -> Unit,
    onChangeGridColumns: (Int) -> Unit,
    viewModel: StartUpNewArticlesViewModels
) {
    var currentGridColumns by remember { mutableIntStateOf(2) }
    var showLabels by remember { mutableStateOf(false) }

    data class FabButton(
        val icon: ImageVector,
        val label: String,
        val onClick: () -> Unit
    )

    val buttons = listOf(
        FabButton(
            Icons.Default.CloudDownload,
            "Import from Firebase"
        ) {
            viewModel.importFromFirebase()
        },
        FabButton(                 //TODO enleve
            Icons.Default.EditCalendar,
            "Filter"
        ) {
            onToggleOutlineFilter()
        },
        FabButton(
            Icons.Default.GridView,
            "Grid"
        ) {
            currentGridColumns = (currentGridColumns % 4) + 1
            onChangeGridColumns(currentGridColumns)
        },
        FabButton(
            if (showLabels) Icons.Default.Close else Icons.Default.Details,
            if (showLabels) "Hide Labels" else "Show Labels"
        ) {
            showLabels = !showLabels
        },
        FabButton(
            Icons.Default.Home,
            "Home"
        ) {
            onToggleNavBar()
        }
    )

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttons.forEach { button ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                AnimatedVisibility(
                    visible = showLabels,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Surface(
                        modifier = Modifier.padding(end = 8.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = button.label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                FloatingActionButton(
                    onClick = button.onClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(button.icon, contentDescription = button.label)
                }
            }
        }
    }
}


//data class ButtonInfo(
//    val icon: ImageVector,
//    val description: String,
//    val color: Color,
//    val onClick: () -> Unit
//)
//
//@Composable
//fun FloatingActionButtons(
//    uiState: CreatAndEditeInBaseDonnRepositeryModels,
//    showFloatingButtons: Boolean,
//    onToggleNavBar: () -> Unit,
//    onToggleFloatingButtons: () -> Unit,
//    onToggleFilter: () -> Unit,
//    onToggleOutlineFilter: () -> Unit,
//    showOnlyWithFilter: Boolean,
//    viewModel: HeadOfViewModels,
//    onChangeGridColumns: (Int) -> Unit,
//    onToggleModeClickDispo: () -> Unit,
//    onCategorySelected: (CategoriesTabelleECB) -> Unit
//) {
//    var showDialogeDataBaseEditer by remember { mutableStateOf(false) }
//    var currentGridColumns by remember { mutableIntStateOf(2) }
//    var showContentDescription by remember { mutableStateOf(false) }
//    var showModeClickDispo by remember { mutableStateOf(false) }
//    var showCategorySelection by remember { mutableStateOf(false) }
//    var showWindosFunctions by remember { mutableStateOf(false) }
//    val maxGridColumns = 4
//
//    Column {
//        if (showFloatingButtons) {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                verticalArrangement = Arrangement.Bottom,
//                horizontalAlignment = Alignment.End
//            ) {
//                FloatingButton(
//                    ButtonInfo(
//                        icon = if (showModeClickDispo) Icons.Default.Close else Icons.Default.Person,
//                        description = "Mode Click Dispo",
//                        color = Color(0xFFE91E63),
//                        onClick = {
//                            onToggleModeClickDispo()
//                            showModeClickDispo = !showModeClickDispo
//                        }
//                    ),
//                    showContentDescription
//                )
//
//                if (!showModeClickDispo) {
//                    Box(
//                        modifier = Modifier
//                            .heightIn(max = 300.dp) // Maximum height for the scrollable area
//                            .verticalScroll(rememberScrollState())
//                    ) {
//                        Column {
//                            val buttons = listOf(
//                                ButtonInfo(
//                                    icon = Icons.Default.Try,
//                                    description = "Update Colors Manager",
//                                    color = Color(0xFF452719)
//                                ) { showWindosFunctions = true },
//                                ButtonInfo(
//                                    icon = Icons.Default.CalendarViewMonth,
//                                    description = "Category Selection",
//                                    color = Color(0xFF9C27B0)
//                                ) { showCategorySelection = true },
//                                ButtonInfo(
//                                    icon = Icons.Default.EditCalendar,
//                                    description = "Outline Filter",
//                                    color = Color(0xFF2196F3),
//                                    onClick = onToggleOutlineFilter
//                                ),
//                                ButtonInfo(
//                                    icon = Icons.Default.Home,
//                                    description = "Home",
//                                    color = Color(0xFF4CAF50),
//                                    onClick = onToggleNavBar
//                                ),
//                                ButtonInfo(
//                                    icon = if (showOnlyWithFilter) Icons.Default.FilterList else Icons.Default.FilterListOff,
//                                    description = "Filter",
//                                    color = Color(0xFFFFC107),
//                                    onClick = onToggleFilter
//                                ),
//                                ButtonInfo(
//                                    icon = Icons.Default.PermMedia,
//                                    description = "Database Editor",
//                                    color = Color(0xFFFF5722)
//                                ) { showDialogeDataBaseEditer = true },
//                                ButtonInfo(
//                                    icon = Icons.Default.GridView,
//                                    description = "Change Grid",
//                                    color = Color(0xFF795548)
//                                ) {
//                                    currentGridColumns = (currentGridColumns % maxGridColumns) + 1
//                                    onChangeGridColumns(currentGridColumns)
//                                },
//                                ButtonInfo(
//                                    icon = if (showContentDescription) Icons.Default.Close else Icons.Default.Details,
//                                    description = "Toggle Description",
//                                    color = Color(0xFF607D8B)
//                                ) {
//                                    showContentDescription = !showContentDescription
//                                }
//                            )
//
//                            buttons.forEach { buttonInfo ->
//                                FloatingButton(buttonInfo, showContentDescription)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        FloatingActionButton(
//            onClick = onToggleFloatingButtons,
//            containerColor = Color(0xFF3F51B5)
//        ) {
//            Icon(
//                if (showFloatingButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
//                contentDescription = "Toggle Floating Buttons"
//            )
//        }
//    }
//
//    if (showCategorySelection) {
//        CategoryReorderAndSelectionWindow(
//            uiState = uiState,
//            viewModel = viewModel,
//            onDismiss = { showCategorySelection = false },
//            onCategorySelected = { category ->
//                onCategorySelected(category)
//                showCategorySelection = false
//            }
//        )
//    }
//
//    if (showWindosFunctions) {
//        WindosFunctions(
//            onDismiss = { showWindosFunctions = false },
//            viewModel = viewModel,
//        )
//    }
//}
//
//@Composable
//private fun FloatingButton(
//    buttonInfo: ButtonInfo,
//    showContentDescription: Boolean
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier.padding(bottom = 16.dp)
//    ) {
//        if (showContentDescription) {
//            Card(
//                modifier = Modifier
//                    .padding(end = 8.dp)
//                    .heightIn(min = 30.dp)
//            ) {
//                Box(
//                    contentAlignment = Alignment.CenterStart,
//                    modifier = Modifier.padding(horizontal = 8.dp)
//                ) {
//                    Text(
//                        text = buttonInfo.description,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
//            }
//        }
//        FloatingActionButton(
//            onClick = buttonInfo.onClick,
//            modifier = Modifier.size(56.dp),
//            containerColor = buttonInfo.color
//        ) {
//            Icon(buttonInfo.icon, contentDescription = buttonInfo.description)
//        }
//    }
//}
//@Composable
//fun WindosFunctions(
//    onDismiss: () -> Unit,
//    viewModel: HeadOfViewModels,
//) {
//    Dialog(
//        onDismissRequest = onDismiss,
//        properties = DialogProperties(usePlatformDefaultWidth = false)
//    ) {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            shape = MaterialTheme.shapes.large
//        ) {
//            Button(
//                onClick = {
//                    viewModel.updateArticleCategories()
//                    onDismiss()
//                }
//            ) {
//                Text("Update Article Colors")
//            }
//            Button(
//                onClick = {
//                    viewModel.updateArticleCategoriesId()
//                    onDismiss()
//                }
//            ) {
//                Text("Update Article Colors Id")
//            }
//        }
//    }
//}
//
