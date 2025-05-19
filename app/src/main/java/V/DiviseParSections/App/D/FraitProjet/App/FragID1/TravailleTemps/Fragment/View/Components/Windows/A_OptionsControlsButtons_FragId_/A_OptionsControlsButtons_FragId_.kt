package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_

import V.DiviseParSections.App.C_AtelieModbile.Fragment.ViewModel.TarificationViewModel
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.LabelsButton
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.MenuButton
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun A_OptionsControlsButtons_FragId_(
    tarificationViewModel: TarificationViewModel = koinViewModel(),
    viewModel: Windows__ViewModel,
) {
    val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showMenu) {

                    if (isAbdelwahabLeGerant) {
                        FragID_0_Butt_2(viewModel, showLabels, "Add Day")
                        FragID_0_Butt_1(viewModel, showLabels, "Start Recording")
                    }

                    FragID_0_Butt_3(viewModel, showLabels, "Mode Admin")

                    TarriffesButtons(
                        showLabels, tarificationViewModel
                    )

                    LabelsButton(
                        showLabels = showLabels,
                        onShowLabelsChange = { showLabels = it }
                    )
                }

                MenuButton(
                    showLabels = showLabels,
                    showMenu = showMenu,
                    onShowMenuChange = { showMenu = it }
                )
            }
        }
    }
}

@Composable
private fun TarriffesButtons(showLabels: Boolean, tarificationViewModel: TarificationViewModel) {
    val uiState = tarificationViewModel.uiState.value
    val activeProduit = uiState.produitsNoSqlDataBase.produits.find { it.itsActiveOne }
    val activeClient = activeProduit?.clientAchteurs?.find { it.itsActiveOne }
    val activeTypeTarification = activeClient?.typeTarification?.find { it.itsActiveOne }
    val tariffsList = activeTypeTarification?.tariffsList ?: emptyList()

    tariffsList.forEach { tariff ->
        // Find the parent TypeTarification
        val parentTypeTarificationId = activeTypeTarification?.infosId ?: 0L
        val relatedTypeInfos = tarificationViewModel.getByID_C_TypeTarificationInfos(
            parentTypeTarificationId
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Use the icon and color from the related TypeTarification
            val couleurButton = relatedTypeInfos?.entityCorrespond?.couleur ?: Color(0xFFF44336)
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.size(40.dp),
                containerColor = couleurButton
            ) {
                relatedTypeInfos?.entityCorrespond?.icon?.let { iconVector ->
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null
                    )
                }
            }
            if (showLabels) {
                Text(
                    "${tariff.valeur}",
                    modifier = Modifier
                        .background(couleurButton)
                        .padding(4.dp),
                    color = Color.White
                )
            }
        }
    }
}
