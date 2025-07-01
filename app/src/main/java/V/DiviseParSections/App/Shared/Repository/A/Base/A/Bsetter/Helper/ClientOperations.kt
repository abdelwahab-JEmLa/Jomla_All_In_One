// File: BSetterFacade/Helper/ClientOperations.kt
package V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper

import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import V.DiviseParSections.App.Shared.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.HClientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientOperations(
    private val getter: AGetter,

    private val hClientRepository: HClientRepository
) {
    val zAppComptRepositoryComposable = getter.zAppComptRepositoryComposable

    fun update_bOuvertDialogMapMarqueHClientKey(clientID: Long) {
        val clientKey = hClientRepository.datasValue.find { it.id == clientID }?.keyID

        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt

        val zCompt =
            clientKey?.let {
                currentZCompt?.copy(
                    bOuvertDialogMapMarqueHClientKey = it
                )
            }

        if (zCompt != null) {
            zAppComptRepositoryComposable.addOrUpdateData(zCompt)
        }
    }

    fun ouvreExistedDataEtNavigatePanie(keyID: String) {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            onVentGBonVentKeyId = keyID,
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.addOrUpdateData(zCompt)
        }
        navigateToCartScreen()
    }

    fun navigateToCartScreen() {
        getter.composScope.launch(Dispatchers.Main) {

        }
    }


    fun client(clientOldID:Long) = hClientRepository.datasValue.find { it.id ==clientOldID }

    fun deleteAddMultiClients() {
        val datas = hClientRepository.datasValue
        CoroutineScope(Dispatchers.IO).launch {
            hClientRepository.dataBaseFactoryFClient.dao.deleteAll()
            hClientRepository.dataBaseFactoryFClient.dao.insertAll(datas)

            HClientInfos.safeRemoveRef()

            hClientRepository.dataBaseFactoryFClient.batchFireBaseUpdate(
                datas
            )
        }
    }
}
