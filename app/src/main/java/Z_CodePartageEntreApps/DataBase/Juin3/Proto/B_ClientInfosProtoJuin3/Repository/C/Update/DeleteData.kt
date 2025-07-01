    package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update

    import V.DiviseParSections.App.Shared.Repository.ID2HClientInfos.Repository.HClientInfos
    import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.DataBaseFactoryFClient
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch

    fun DataBaseFactoryFClient.deleteData(data: HClientInfos) {
        CoroutineScope(Dispatchers.IO).launch {
            val preparedData = data.withProperKeyFireBaseAndTimeTamp()

            dao.deleteData(preparedData)

            HClientInfos.removeRef(preparedData)

            val allData = dao.getAll()
            updateRepoState(allData)
        }
    }
