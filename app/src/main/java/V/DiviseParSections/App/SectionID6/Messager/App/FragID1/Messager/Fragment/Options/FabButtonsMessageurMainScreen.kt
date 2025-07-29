package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.LabelsButton
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.MenuButton
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun FabButtonsMessageurMainScreen(
    viewModel: ViewModelMessageur,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    var showMenuButtons by remember { mutableStateOf(false) }

    var showMenu by remember { mutableStateOf(true) }
    var showLabels by remember { mutableStateOf(true) }

    // State for the text input functionality
    var showTextInput by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf("") }

    // RepositorysMainGetter screen configuration to position at the right edge
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 180f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value + 100f) }

    val parent_M9AppCompt_KeyID = (focusedValuesGetter
        .currentActive_M9AppCompt?.keyID
        ?: "")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    focusedValuesGetter.currentApp_Est_Admin.ifTrue {
                        Button(
                            modifier = Modifier
                                .semantics(mergeDescendants = true) {
                                    set(
                                        value = parent_M9AppCompt_KeyID,
                                        key = SemanticsPropertyKey("parent_M9AppCompt_KeyID")
                                    )
                                },
                            onClick = {
                                val new_Data = M17MessageVocale.get_default()
                                    .copy(
                                        parent_M9AppCompt_KeyID = parent_M9AppCompt_KeyID,
                                        etate = M17MessageVocale.Etate.ENVOYER,
                                        creationTimestamps = System.currentTimeMillis(),
                                        its_Text_Message = true,
                                        text_Inputted = textValue
                                    )

                                repositorysMainSetter.upsert_M17MessageVocale(
                                    new_Data
                                )
                            }
                        ) {
                            Text(
                                "👍", fontSize = 20.sp
                            )
                        }
                        ButtonMessageVocale(
                            viewModel = viewModel
                        )
                    }
                }

                if (showMenuButtons) {
                    LabelsButton(
                        showLabels = showLabels,
                        onShowLabelsChange = { showLabels = it }
                    )

                    MenuButton(
                        showLabels = showLabels,
                        showMenu = showMenu,
                        onShowMenuChange = { showMenu = it }
                    )
                }
            }
        }
    }
}
