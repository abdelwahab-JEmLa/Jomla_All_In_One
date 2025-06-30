package V.DiviseParSections.App.Shared.Modules.Ui.B.UI

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.ACentral
import V.DiviseParSections.App.Shared.Repository.AGetter
import V.DiviseParSections.App.Shared.Repository.AGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import Views.Common.Components.ToastData
import Views.Common.Components.ToastType
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Stable
class DebugKey(
     val getter: AGetter,
) {
    val hClientRepository = getter.hClientRepository

     var keyByParent = mutableStateOf("")

    fun update_keyByParent(data :String): Unit {
        keyByParent.value=data
    }
    fun getKeyID8BonVent(
        clientOldID: Long? = null,
        etate: GBonVent.EtateActuellementEst? = null,
    ): String {
        val activePeriodKeyByParent = getter.parametresAppComptNonSaved.activePeriodKeyByParent
        val keyModelToOnVentHVentPeriodKeyByParent =
            Z_AppCompt.keyModelValID7 + "-" + activePeriodKeyByParent

        val keyModelToClientKeyByParent =
            clientOldID?.let { HClientInfos.keyModel + "-" + hClientRepository.datasValue.find { it.id == clientOldID }?.getTempKeyByParent() }
        val keyModelToEtateKey =
            etate?.let { "--" + GBonVent.EtateActuellementEst.keyModel + "-" + it.name }
                ?: ""

        return ("$keyModelToOnVentHVentPeriodKeyByParent--$keyModelToClientKeyByParent$keyModelToEtateKey")
            .withOutFireBaseInvalidCharacters()
    }
}

class ViewModelDebugFloatingButton(
    val ACentral: ACentral,
) : ViewModel() {
    val debugKey = ACentral.modulesCentral.debugKey
    data class UiState(
        val clickSurElementAfficheKey: Boolean = true,
        val showButtons: Boolean = true,
        val showLabels: Boolean = true,
        val offsetX: Float = 0f,
        val offsetY: Float = 0f,
        val currentToast: ToastData? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateOffset(deltaX: Float, deltaY: Float) {
        _uiState.value = _uiState.value.copy(
            offsetX = _uiState.value.offsetX + deltaX,
            offsetY = _uiState.value.offsetY + deltaY
        )
    }

    fun showToast(message: String, type: ToastType = ToastType.INFO, duration: Long = 1500L) {
        _uiState.value = _uiState.value.copy(
            currentToast = ToastData(
                message = message,
                type = type,
                duration = duration
            )
        )
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(currentToast = null)
    }
}

@Composable
fun DebugFloatingButton(
    viewModel: ViewModelDebugFloatingButton = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()


    if (uiState.showButtons) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            uiState.offsetX.roundToInt(),
                            uiState.offsetY.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            viewModel.updateOffset(dragAmount.x, dragAmount.y)
                        }
                    }
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button1R(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun Button1R(
    viewModel: ViewModelDebugFloatingButton,
) {
    val uiState by viewModel.uiState.collectAsState()

    val debugKey = viewModel.debugKey
    val buttonBackgroundColor = Color(0xFF9C27B0)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = {

            },
            modifier = Modifier.size(40.dp),
            containerColor = buttonBackgroundColor,
        ) {
            val iconColor = Color.Black

            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = null,
                tint = iconColor
            )
        }

        if (uiState.showLabels) {
            Text(
                text = "uiState.keyText",
                modifier = Modifier
                    .background(buttonBackgroundColor)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
