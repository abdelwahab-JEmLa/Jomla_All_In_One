package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package
     /*
fun groupeAchatsParIdCouleurEtAddAu_4_CouleurOperationCommand() {
        _uiStateFlow.value = _uiStateFlow.value.copy(isDataLoading = true)

        _0_0_HeadOfRepositorys_Repository.repositorys_Model._4_CouleurOperationCommand_Repository.deleteAllEtRestartSequenceces()
        _uiStateFlow.value._4_CouleurOperationCommand.clear()

        val filteredCouleurOperations = a_1_1_CouleurAcheteOperation.filter {
            it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
        }

        val filteredProduitOperations = b_1_2_ProduitAcheteOperation.filter {
            it.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
        }

        if (filteredCouleurOperations.isEmpty()) {
            _uiStateFlow.value = _uiStateFlow.value.copy(isDataLoading = false)
            return
        }

        val produitIdMap = mutableMapOf<Long, Any>()
        for (produit in _2_1_ProduitsDataBase) {
            produitIdMap[produit.vid] = produit
        }

        val produitAcheteToRealProductMap = mutableMapOf<Long, Long>()
        for (produitAchete in filteredProduitOperations) {
            produitAcheteToRealProductMap[produitAchete.vid] = produitAchete.produitAcheterID
        }

        val groupedOperations = mutableMapOf<Pair<Long, Long?>, MutableList<_1_1_CouleurAcheteOperation>>()

        for (operation in filteredCouleurOperations) {
            val parentProductId = operation.parentProduitAchateOperationVID
            val realProductID = parentProductId?.let { produitAcheteToRealProductMap[it] }
            val validProduitVID = if (realProductID != null && produitIdMap.containsKey(realProductID)) {
                realProductID
            } else {
                parentProductId
            }

            val key = Pair(operation.couleurIndex_ParentVID, validProduitVID)
            if (!groupedOperations.containsKey(key)) {
                groupedOperations[key] = mutableListOf()
            }
            groupedOperations[key]?.add(operation)
        }

        val commands = groupedOperations.map { (keyPair, operations) ->
            _4_CouleurOperationCommand(
                couleurIndex_ParentVID = keyPair.first,
                produitVID_ParentKey = keyPair.second,
                totaleQuantity = operations.sumOf { it.totaleQuantity }
            )
        }

        if (commands.isNotEmpty()) {
            repositorys_Model._4_CouleurOperationCommand_Repository.addMultiDATAsEtReturnVIDsList(commands) { vidsList ->
                commands.forEachIndexed { index, command ->
                    if (index < vidsList.size) {
                        command.vid = vidsList[index]
                    }
                }

                _uiStateFlow.value = _uiStateFlow.value.copy(
                    _4_CouleurOperationCommand = mutableStateListOf<_4_CouleurOperationCommand>().apply {
                        addAll(commands)
                    }
                )
            }
        }

        _uiStateFlow.value = _uiStateFlow.value.copy(isDataLoading = false)
    }
   */
