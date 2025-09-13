package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.f.z.Com

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@Composable
fun ToggleButton_MoveToStorePosition(
    produit: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val isSelected =
        activeCentralValues.held_Produit_Pour_Move_Au_Position_Store?.keyID == produit.keyID

    IconButton(
        onClick = {
            val newHeldProduit = if (isSelected) null else produit
            focusedValuesGetter.update_activeCentralValues(
                activeCentralValues.copy(
                    held_Produit_Pour_Move_Au_Position_Store = newHeldProduit
                )
            )
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Filled.Cancel else Icons.Filled.Upload,
            contentDescription = if (isSelected) "Désélectionner" else "Sélectionner pour déplacer",
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

