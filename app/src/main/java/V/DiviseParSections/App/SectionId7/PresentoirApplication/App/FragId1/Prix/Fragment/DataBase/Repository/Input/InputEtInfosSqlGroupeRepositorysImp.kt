package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.Test.ClientTestData
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.Test.ProduitTestData
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.Test.TarificationTestData
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.Test.TypeTarificationTestData
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InputEtInfosSqlGroupeRepositorysImp : InputEtInfosSqlGroupeRepositorys {
    private val produitRepository = ProduitDataBase_RepositoryImp()
    private val clientRepository = ClientDataBase_RepositoryImp()
    private val typeTarificationRepository = TypeTarificationDataBase_RepositoryImp()
    private val tarificationRepository = TarificationRepositoryImp()

    override fun ProduitInfosRepository(): InputEtInfosSqlGroupeRepositorys.ProduitDataBase_Repository {
        return produitRepository
    }

    override fun ClientDataBase_Repository(): InputEtInfosSqlGroupeRepositorys.ClientDataBase_Repository {
        return clientRepository
    }

    override fun TypeTarificationInfosRepository(): InputEtInfosSqlGroupeRepositorys.TypeTarificationDataBase_Repository {
        return typeTarificationRepository
    }

    override fun TarificationRepository(): InputEtInfosSqlGroupeRepositorys.TarificationRepository {
        return tarificationRepository
    }

    class ProduitDataBase_RepositoryImp :
        InputEtInfosSqlGroupeRepositorys.ProduitDataBase_Repository {
        override var modelList: List<InputEtInfosSqlModels.ProduitInfos> = initDefaultData()

        private fun initDefaultData(): List<InputEtInfosSqlModels.ProduitInfos> {
            return mutableStateListOf<InputEtInfosSqlModels.ProduitInfos>().apply {
                addAll(ProduitTestData.initialTestData)
            }
        }
    }

    class ClientDataBase_RepositoryImp :
        InputEtInfosSqlGroupeRepositorys.ClientDataBase_Repository {
        override var modelList: List<InputEtInfosSqlModels.ClientDataBase> = initDefaultData()

        private fun initDefaultData(): List<InputEtInfosSqlModels.ClientDataBase> {
            return mutableStateListOf<InputEtInfosSqlModels.ClientDataBase>().apply {
                addAll(ClientTestData.initialTestData)
            }
        }

        override fun add(client: InputEtInfosSqlModels.ClientDataBase) {
            val list = modelList as? MutableList ?: return
            val existingIndex = list.indexOfFirst { it.id == client.id }
            if (existingIndex == -1) {
                list.add(client)
            } else {
                list[existingIndex] = client
            }
        }

        override fun update(
            client: InputEtInfosSqlModels.ClientDataBase,
            onSuccess: (InputEtInfosSqlModels.ClientDataBase) -> Unit
        ) {
            val list = modelList as? MutableList ?: return
            val index = list.indexOfFirst { it.id == client.id }
            if (index != -1) {
                list[index] = client
                onSuccess(client)
            }
        }
    }

    class TypeTarificationDataBase_RepositoryImp :
        InputEtInfosSqlGroupeRepositorys.TypeTarificationDataBase_Repository {
        override var modelList: List<InputEtInfosSqlModels.TypeTarificationDataBase> = initDefaultData()

        private fun initDefaultData(): List<InputEtInfosSqlModels.TypeTarificationDataBase> {
            return mutableStateListOf<InputEtInfosSqlModels.TypeTarificationDataBase>().apply {
                addAll(TypeTarificationTestData.initialTestData)
            }
        }
    }

    class TarificationRepositoryImp :
        InputEtInfosSqlGroupeRepositorys.TarificationRepository {
        val _dataFlow = MutableStateFlow(TarificationTestData.initialTestData)
        override var modelList: List<InputEtInfosSqlModels.Tarification>
            get() = _dataFlow.value
            set(value) {
                _dataFlow.value = value
            }

        override fun add(
            data: InputEtInfosSqlModels.Tarification,
            onSuccess: (InputEtInfosSqlModels.Tarification) -> Unit
        ) {
            _dataFlow.update { currentList ->
                currentList + data
            }
            onSuccess(data)
        }
    }
}
