// File: BSetterFacade/Helper/ClientOperations.kt
package V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper

import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientOperations(
    private val getter: AGetter,

    private val hClientRepository: Repo2Client
) {
    val zAppComptRepositoryComposable = getter.repo9AppCompt

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
            zAppComptRepositoryComposable.upsert(zCompt)
        }
    }

    fun ouvreExistedDataEtNavigatePanie(keyID: String) {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            onVentM8BonVentKey = keyID,
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.upsert(zCompt)
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
            hClientRepository.dataBaseCreationFactory.dao.deleteAll()
            hClientRepository.dataBaseCreationFactory.dao.insertAll(datas)

            HClientInfos.safeRemoveRef()

            hClientRepository.dataBaseCreationFactory.batchFireBaseUpdate(
                datas
            )
        }
    }
}
