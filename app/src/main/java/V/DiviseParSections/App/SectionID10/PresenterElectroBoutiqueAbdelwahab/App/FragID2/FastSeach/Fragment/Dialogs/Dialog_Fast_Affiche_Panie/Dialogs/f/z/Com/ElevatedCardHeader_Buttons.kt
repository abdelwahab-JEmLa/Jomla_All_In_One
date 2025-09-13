package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.f.z.Com

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
            imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = if (isSelected) "Désélectionner" else "Sélectionner pour déplacer",
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MoveUpButton(
    produit: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = koinInject()
) {
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val shouldShow = activeCentralValues.held_Produit_Pour_Move_Au_Position_Store != null

    if (shouldShow) {
        IconButton(
            onClick = {
                activeCentralValues.held_Produit_Pour_Move_Au_Position_Store?.let { heldProduit ->
                    val newPosition = (heldProduit.position_store_3jamale ?: 0) - 1
                    repositorysMainSetter.upsert_M1Produit(
                        heldProduit.copy(
                            position_store_3jamale = newPosition,
                            dernier_timeTamps_position_store_3jamale = System.currentTimeMillis()
                        )
                    )
                }
            },
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Déplacer vers le haut",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun InfoButton(
    productName: String,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    IconButton(
        onClick = {
            val activeCentralValues = focusedValuesGetter.active_Central_Values
            focusedValuesGetter.update_activeCentralValues(
                activeCentralValues.copy(
                    held_Produit_Pour_Move_Au_Position_Store = null,
                    fastSearchProduitPourVent = productName,
                    affiche_Dialog_Fast_Affiche_Panie = true
                )
            )
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "Informations du produit",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ToggleButton_PremierCheckDonne(
    ventList: List<M10OperationVentCouleur>,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine current state: true if ALL items have premier_Check_Donne = true
    val allChecked = remember(ventList) {
        ventList.isNotEmpty() && ventList.all { it.premier_Check_Donne }
    }

    // Determine what the new state should be when toggled
    val newStateWhenToggled = !allChecked

    IconButton(
        onClick = { onToggle(newStateWhenToggled) },
        modifier = modifier
    ) {
        Icon(
            imageVector = if (allChecked) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = if (allChecked) "Masquer les vérifications" else "Afficher les vérifications",
            tint = if (allChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
