package V.DiviseParSections.App.Shared.Repository

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.VentOperations
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.BonVentOperations
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference

class BSetter(
    private val produitOperations: ProduitOperations,
    val id8BonVentOperations: BonVentOperations,
    private val clientOperations: ClientOperations,
    private val ventOperations: VentOperations,
) {
    fun ouvrireNewAppComptOnVentBonVentEtAddLe(clientOldId: Long, newEtate: GBonVent.EtateActuellementEst = GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT) = id8BonVentOperations.ouvrireNewAppComptOnVentBonVentEtAddLe(clientOldId, newEtate)

    fun dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey() = id8BonVentOperations.dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey()

    fun ajouteNewBonVent(key: String, clientOldId: Long, etate: GBonVent.EtateActuellementEst) = id8BonVentOperations.ajouteNewBonVent(key, clientOldId, etate)

    fun updateComptAppErExistKey(key: String, clientOldId: Long, etate: GBonVent.EtateActuellementEst) = id8BonVentOperations.updateComptAppErExistKey(key, clientOldId, etate)

    fun clear_onVentGBonVentKeyId_EtbOuvertDialogMapMarqueHClientKey() = id8BonVentOperations.clear_onVentGBonVentKeyId_EtbOuvertDialogMapMarqueHClientKey()

    fun cleanFermeAppComptOnVentBonVent() = id8BonVentOperations.clear_bOuvertDialogMapMarqueHClientKey()

    fun update_bOuvertDialogMapMarqueHClientKey(clientID: Long) = clientOperations.update_bOuvertDialogMapMarqueHClientKey(clientID)

    fun ouvreExistedDataEtNavigatePanie(keyID: String) = clientOperations.ouvreExistedDataEtNavigatePanie(keyID)

    fun deleteAddMultiClients() = clientOperations.deleteAddMultiClients()
    fun deleteAddMultiDatas() = produitOperations.deleteAddMultiDatas()

    fun getKeyID8BonVent(clientId: Long, etate: GBonVent.EtateActuellementEst): String = id8BonVentOperations.getKeyID8BonVent(clientId, etate)

    fun upsertBonVent(keyHandBonVent: String) = id8BonVentOperations.upsertBonVent(keyHandBonVent)

    fun acheterACaSetterCentral(fCouleurVentOperation: FCouleurVentOperationInfos? = null, produit: ArticlesBasesStatsTable, colorIndex: Int, quantity: Int) = ventOperations.acheterACaSetterCentral(fCouleurVentOperation, produit, colorIndex, quantity)

    fun updateListRelativeVentCouleurPrixVent(produitKey: String?, newPrix: Double) = ventOperations.updateListRelativeVentCouleurPrixVent(produitKey, newPrix)

    fun deleteVents(parentProduitOldId: Long) = ventOperations.deleteVents(parentProduitOldId)

    fun toggleEtateDeliveryNonTrouveVentOu(produitKey: String) = ventOperations.toggleEtateDeliveryNonTrouveVentOu(produitKey)

    companion object {
        fun regexReturnParentKeysMap(input: String): Map<String, String> =
            Regex("(\\w+)-(\\w+)").findAll(input).associate { match ->
            val (key, value) = match.destructured
            key to value
        }

        fun genereUnPushKeyFireBase(ref: DatabaseReference): String { return ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key") }    }
}

@Composable
fun ViewClientKeyByParenComposable(
    viewClientKeyByParent: String,
) {
    Card(
        modifier = Modifier
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = viewClientKeyByParent,
                color = MaterialTheme.colorScheme.onError,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2
            )
        }
    }
}
