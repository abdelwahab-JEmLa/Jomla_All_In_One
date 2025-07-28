// File: RepositorysMainSetter/Helper/ClientOperations.kt
package V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientOperations(
    private val getter: RepositorysMainGetter,

    private val hClientRepository: Repo2Client
) {
    val zAppComptRepositoryComposable = getter.repo9AppCompt


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

            M2Client.safe_Remove_MainDatas_Ref()

            hClientRepository.dataBaseCreationFactory.batchFireBaseUpdate(
                datas
            )
        }
    }
}
