    package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Update

    import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
    import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch

    fun dataBaseCreationFactoryMID2ClientRepository.deleteData(data: M2Client) {
        CoroutineScope(Dispatchers.IO).launch {
            val preparedData = data.with_Trigger_RealTime()

            dao.deleteData(preparedData)

            M2Client.removeRef(preparedData)

            val allData = dao.getAll()
            updateRepoState(allData)
        }
    }
