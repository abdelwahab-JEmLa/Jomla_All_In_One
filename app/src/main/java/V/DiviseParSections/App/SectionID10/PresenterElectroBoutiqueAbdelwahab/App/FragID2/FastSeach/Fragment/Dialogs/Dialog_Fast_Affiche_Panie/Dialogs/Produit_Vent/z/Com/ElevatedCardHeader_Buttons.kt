package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent.z.Com

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun InfoButton(
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    appDatabase: AppDatabase= koinInject (),
    produit: M01Produit
) {
    val coroutineScope = rememberCoroutineScope()
    val allAppCompts by appDatabase.dao_M9AppCompt().getAllFlow().collectAsState(initial = emptyList())
    val m9: Z_AppCompt? = allAppCompts.find {
        it.keyID == M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
    }
    val affiche_Dialog_Fast_Affiche_Panie_App4: Boolean? = m9?.affiche_Dialog_Fast_Affiche_Panie_App4

    IconButton(
        onClick = {
            val activeCentralValues = focusedValuesGetter.active_Central_Values
            focusedValuesGetter.update_activeCentralValues(
                activeCentralValues.copy(
                    held_Produit_Pour_Move_Au_Position_Store = null,
                    fastSearchProduitPourVent = produit.nom,
                    affiche_Dialog_Fast_Affiche_Panie = false
                )
            )
            affiche_Dialog_Fast_Affiche_Panie_App4?.let {
                coroutineScope.launch {
                    appDatabase.dao_M9AppCompt().update(
                        m9.copy(
                            affiche_Dialog_Fast_Affiche_Panie_App4 = false
                        )
                    )
                }
            }
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

