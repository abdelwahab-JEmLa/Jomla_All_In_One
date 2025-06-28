    package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update

    import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.B_ClientInfosProtoJuin3
    import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch

    fun B_ClientInfosProtoJuin3Repository.deleteData(data: B_ClientInfosProtoJuin3) {
        CoroutineScope(Dispatchers.IO).launch {
            val preparedData = data.withProperKeyFireBaseAndTimeTamp()

            dao.deleteData(preparedData)

            B_ClientInfosProtoJuin3.removeRef(preparedData)

            val allData = dao.getAll()
            updateRepoState(allData)
        }
    }
