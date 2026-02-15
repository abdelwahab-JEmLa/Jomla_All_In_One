package V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import Z_CodePartageEntreApps.Modules.CameraHandler.CameraFABProtoJuin3
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun Floating_Separated_FragMap_Button_1_SelectCategorieEtAddNewProduit(
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "",
        icons = Pair(Icons.Default.FilterList, Icons.Default.ViewList),
        colors = Pair(Color.Red, Color.Green)
    )
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val currentVisibleClientsMode = currentValues.visibleClientsNow
    val isShowingAll = currentVisibleClientsMode == MapClientsViewModel.VisibleClientsNow.showAll
    val updatedButtonState = buttonState.copy(its_Active = isShowingAll)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 250f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 350f) }
    var showDropdown by remember { mutableStateOf(false) }

    val catalogues = get_ListM21CataloguesCategorie()

    val activeCatalogue = catalogues.find {
        it.keyID == (currentValues.active_Catalogue_Pour_NewAddedProduit?.keyID ?: 0)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        offsetX = offsetX.coerceIn(0f, screenWidth.value - 150f)
                        offsetY = offsetY.coerceIn(0f, screenHeightDp.value - 150f)
                    }
                }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (updatedButtonState.showLabels) {
                    Text(
                        text = activeCatalogue?.nom ?: "RRR",
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                color = (activeCatalogue?.couleur ?: Color.Gray).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Main category selection button
                FloatingActionButton(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            set(
                                value = activeCatalogue,
                                key = SemanticsPropertyKey("activeCategorie")
                            )
                        }
                        .size(48.dp),
                    onClick = { showDropdown = true },
                    containerColor = if (updatedButtonState.its_Active)
                        updatedButtonState.colors.second
                    else
                        updatedButtonState.colors.first
                ) {
                    Icon(
                        imageVector = if (updatedButtonState.its_Active)
                            updatedButtonState.icons.second
                        else
                            updatedButtonState.icons.first,
                        contentDescription = if (isShowingAll) "Switch to Targeted View" else "Switch to Show All",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                FloatingActionButton(
                    modifier = Modifier.size(40.dp),
                    onClick = {
                        // Toggle the active_EtateDispoNonDifinieAuAddNew state
                        val newValues = currentValues.copy(
                            active_EtateDispoNonDifinieAuAddNew = !currentValues.active_EtateDispoNonDifinieAuAddNew
                        )
                        focusedValuesGetter.update_activeCentralValues(newValues)
                    },
                    containerColor = if (currentValues.active_EtateDispoNonDifinieAuAddNew)
                        Color(0xFFFF9800) // Orange when active
                    else
                        Color(0xFF757575) // Gray when inactive
                ) {
                    Icon(
                        imageVector = if (currentValues.active_EtateDispoNonDifinieAuAddNew)
                            Icons.Default.ToggleOn
                        else
                            Icons.Default.ToggleOff,
                        contentDescription = "Toggle État Disponibilité Non Définie",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Camera FAB
                CameraFABProtoJuin3()

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    catalogues.forEach { catalogue ->
                        val newValues =
                            currentValues.copy(active_Catalogue_Pour_NewAddedProduit = catalogue)

                        DropdownMenuItem(
                            modifier = Modifier.semantics(mergeDescendants = true) {
                                set(value = newValues, key = SemanticsPropertyKey("newValues"))
                            },
                            text = {
                                Text(
                                    text = catalogue.nom,
                                    color = if (activeCatalogue?.id == catalogue.id)
                                        catalogue.couleur else Color.Black
                                )
                            },
                            onClick = {
                                focusedValuesGetter.update_activeCentralValues(newValues)
                                showDropdown = false
                            }
                        )
                    }
                }
            }
        }
    }
}
