package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent.z.Com

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ToggleButton_PremierCheckDonne(
    ventList: List<M10OperationVentCouleur>,
    onToggle: (Boolean) -> Unit,
    positionIndex: Int,
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject() ,
    repositorysMainGetter: RepositorysMainGetter=koinInject()
) {
    val context = LocalContext.current
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val activeCentralValues = focusedValuesGetter.active_Central_Values

    val isSecurityDisabled = activeCentralValues.le_pourvoire_clike_checked_est_active

    val allChecked = remember(ventList) {
        ventList.isNotEmpty() && ventList.all { it.premier_Check_Donne }
    }

    val newStateWhenToggled = !allChecked

    FloatingActionButton(
        onClick = {
            if (isSecurityDisabled) {
                if (ventList.isNotEmpty()) {
                    val productInfo = ventList.joinToString("\n") { vent ->
                        "${repositorysMainGetter.find_M1Produit_ByKeyID(vent.parent_M1Produit_KeyId)?.nom}: ${vent.quantity} unités"
                    }
                    val statusText = if (newStateWhenToggled) "Marqué comme vérifié" else "Marquage retiré"
                    Toast.makeText(
                        context,
                        "Position $positionIndex\n$statusText\n\n$productInfo",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                val repo10 = aCentralFacade.repositorysMainSetter.repo10OperationVentCouleur
                val currentTimestamp = System.currentTimeMillis()

                // FIXED: If changing from false to true (checking), also set lence_pour_check to false
                if (newStateWhenToggled) {
                    // Toggle to true AND set lence_pour_check to false
                    ventList.forEach { vent ->
                        val updatedVent = vent.copy(
                            premier_Check_Donne = true,
                            lence_pour_check = false,
                            last_update_premier_Check_Donne_TimeTamps = currentTimestamp
                        )
                        repo10.update_If_Exist(updatedVent)
                    }
                } else {
                    // Just toggle normally (uncheck) with timestamp update
                    ventList.forEach { vent ->
                        val updatedVent = vent.copy(
                            premier_Check_Donne = false,
                            last_update_premier_Check_Donne_TimeTamps = currentTimestamp
                        )
                        repo10.update_If_Exist(updatedVent)
                    }
                }
            } else {
                // Show toast when security is enabled
                Toast.makeText(
                    context,
                    "⚠️ Déverrouillez la sécurité pour modifier (bouton cadenas jaune)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        modifier = modifier.size(48.dp),
        // Visual indication when button is disabled due to security
        containerColor = when {
            !isSecurityDisabled -> Color(0xFFBDBDBD) // Gray when locked
            allChecked -> Color(0xFFFFEB3B) // Yellow when checked
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = when {
            !isSecurityDisabled -> Color(0xFF757575) // Dark gray when locked
            allChecked -> Color.Black
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    ) {
        Text(
            text = if (!isSecurityDisabled) "🔒$positionIndex" else positionIndex.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = when {
                !isSecurityDisabled -> Color(0xFF757575)
                allChecked -> Color.Black
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
