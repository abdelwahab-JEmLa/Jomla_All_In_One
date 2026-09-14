package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent.Companion.sum_totale_et_benifice
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

/**
 * Bouton toujours affichable (indépendant de l'état du dernier bon) qui :
 *  1) recalcule sum_De_Totale_Vents du dernier bon "en commande"
 *     (ON_MODE_COMMEND_ACTUELLEMENT) à partir de ses opérations de vente,
 *  2) enregistre ce total recalculé comme un nouveau Credit,
 *  3) puis pousse une nouvelle situation (New_Situation_Credit) qui ajoute
 *     ce montant à l'ancien solde de dette du client.
 *
 * Reprend la logique de recalcul déjà présente dans AfficheurRegleOuvert et
 * la logique de création credit + new situation déjà présente dans
 * DropdownItem_Credit, mais les enchaîne en une seule action toujours visible
 */
@Composable
fun ButtonRecalculeEtAjouteCreditDepuisCommande(
    aCentralFacade: ACentralFacade,
    relative_M2Client: M2Client?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repositorysMainGetter = aCentralFacade.repositorysMainGetter
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter

    var showConfirmationDialog by remember { mutableStateOf(false) }

    fun recalculeEtAjouteCredit() {
        val client = relative_M2Client ?: run {
            Toast.makeText(context, "لا يوجد زبون محدد", Toast.LENGTH_SHORT).show()
            return
        }
        val currentPeriod = focusedValuesGetter.currentActiveFocuced_M14VentPeriode ?: run {
            Toast.makeText(context, "لا توجد فترة بيع نشطة", Toast.LENGTH_SHORT).show()
            return
        }
        val currentCompt = focusedValuesGetter.currentActive_M9AppCompt ?: run {
            Toast.makeText(context, "لا يوجد حساب نشط", Toast.LENGTH_SHORT).show()
            return
        }

        val repo8BonVent = repositorysMainGetter.repo8BonVent

        // Dernier bon "en commande" pour ce client.
        val lastCommandeBonVent = repo8BonVent.datasValue
            .filter {
                it.parent_M2Client_KeyID == client.keyID &&
                        it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            }
            .maxByOrNull { it.creationTimestamps } ?: run {
            Toast.makeText(context, "لا توجد فاتورة حالية لهذا الزبون", Toast.LENGTH_SHORT).show()
            return
        }

        // 1) Recalcule le total du dernier bon "en commande" à partir de
        // ses opérations de vente, puis persiste.
        val ventsForBon = repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { vent ->
            vent.parent_M8BonVent_KeyId == lastCommandeBonVent.keyID
        }
        val recalculatedSums = lastCommandeBonVent.sum_totale_et_benifice(
            vents = ventsForBon,
            tariffs = emptyList<M13TarificationInfos>(),
        )
        val updatedCommandeBonVent = lastCommandeBonVent.copy(
            sum_De_Totale_Vents = recalculatedSums.totale_vents,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis(),
        )
        repositorysMainSetter.update_M8BonVent(updatedCommandeBonVent)

        // Dernière situation de dette connue pour ce client (avant l'ajout).
        val latestSituationMontant: Double = repo8BonVent.datasValue
            .filter {
                it.parent_M2Client_KeyID == client.keyID &&
                        it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
            }
            .maxByOrNull { it.creationTimestamps }
            ?.montant_principale_du_type ?: 0.0

        val montantCredit = updatedCommandeBonVent.sum_De_Totale_Vents
        val baseTs = System.currentTimeMillis()

        // 2) Enregistre le montant recalculé comme un nouveau Credit,
        // en référençant le bon "en commande" d'origine.
        val creditBon = M8BonVent.get_default(
            parent_M9AppCompt_KeyID = currentCompt.keyID,
            parent_M9AppCompt_DebugInfos = currentCompt.get_DebugInfos(),
            parent_M14VentPeriod_KeyId = currentPeriod.keyID,
            parent_M14VentPeriod_DebugInfos = currentPeriod.get_DebugInfos(),
            parent_M2Client_KeyID = client.keyID,
            parent_M2Client_DebugInfos = client.get_DebugInfos(),
            etateActuellementEst = M8BonVent.EtateActuellementEst.Credit,
        ).copy(
            creationTimestamps = baseTs,
            credit_fait = montantCredit,
            moulahada = "Bon Vent de Key: ${updatedCommandeBonVent.keyID}",
        )

        // 3) Pousse la nouvelle situation de dette = ancienne situation + ce credit.
        val newSituation = M8BonVent.get_default(
            parent_M9AppCompt_KeyID = currentCompt.keyID,
            parent_M9AppCompt_DebugInfos = currentCompt.get_DebugInfos(),
            parent_M14VentPeriod_KeyId = currentPeriod.keyID,
            parent_M14VentPeriod_DebugInfos = currentPeriod.get_DebugInfos(),
            parent_M2Client_KeyID = client.keyID,
            parent_M2Client_DebugInfos = client.get_DebugInfos(),
            etateActuellementEst = M8BonVent.EtateActuellementEst.New_Situation_Credit,
        ).copy(
            creationTimestamps = baseTs + 1_000L,
            montant_principale_du_type = latestSituationMontant + montantCredit,
            moulahada = "Bon Vent de Key: ${updatedCommandeBonVent.keyID}",
        )

        repositorysMainSetter.update_M8BonVent(creditBon)
        repositorysMainSetter.update_M8BonVent(newSituation)

        Toast.makeText(
            context,
            "تم تحديث المجموع و تسجيله كدين جديد",
            Toast.LENGTH_SHORT,
        ).show()
    }

    TextButton(
        onClick = { showConfirmationDialog = true },
        modifier = modifier.fillMaxWidth(),
    ) {
        Text("إعادة حساب الفاتورة الحالية و تسجيلها كدين جديد")
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("تأكيد العملية") },
            text = {
                Text("سيتم إعادة حساب المجموع الكلي للفاتورة الحالية و تسجيله كدين جديد على الزبون. هل تريد المتابعة؟")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        recalculeEtAjouteCredit()
                        showConfirmationDialog = false
                    }
                ) {
                    Text("تأكيد")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
