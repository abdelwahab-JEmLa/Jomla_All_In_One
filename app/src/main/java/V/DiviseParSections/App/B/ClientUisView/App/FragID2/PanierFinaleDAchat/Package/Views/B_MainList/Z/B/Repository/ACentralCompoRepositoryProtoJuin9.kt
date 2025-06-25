    package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository

    import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A1.Proto.Juin17.Proto.WDatabaseInitializationManager
    import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A1.Proto.Juin17.Proto.Z.Repository.Juin9.Proto.Z_ComptAppStateCompoRepositoryProtoAvanJuin17
    import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.A_GroupeValuesA_ProduitsToB_Categories
    import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.B4CatalogueCategoriesRepository
    import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.B_ClientsStateCompoRepository
    import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.C3_TransactionCommercial
    import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.CCategoriesCompoRepository
    import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.ETransactionCommercialCompoRepository
    import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.ZAppCompt_RepositoryComposable
    import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.Z_AppCompt
    import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
    import android.content.Context
    import androidx.compose.runtime.Stable
    import androidx.compose.runtime.derivedStateOf
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableFloatStateOf
    import com.google.firebase.database.DatabaseReference
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch

    @Stable
    class ACentralCompoRepositoryProtoJuin9(
        private val context: Context,
        val databaseInitializationManager: WDatabaseInitializationManager,

        val bProduitDataBase_SubClassFunctionality: BProduitDataBaseComposeRepositoryPJ17,

        val a_GroupeValuesA_ProduitsToB_Categories: A_GroupeValuesA_ProduitsToB_Categories,
        val b3CategoriesCompoRepository: CCategoriesCompoRepository,

        val clientsState: B_ClientsStateCompoRepository,
        val transactionCommercialState: ETransactionCommercialCompoRepository,

        val fCouleurAchatOperationRepositoryComposable: FAchatOperationCouleurRepositoryComposable,
     //   val dCouleurAchatOperationSubClassFunctionality: DSubClassFunctionality_CouleurAchatOperation,

        val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
        val comptAppState: Z_ComptAppStateCompoRepositoryProtoAvanJuin17,

        val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    ) {

        private val composScope = CoroutineScope(Dispatchers.IO)
        private val _loadingProgress = mutableFloatStateOf(0f)
        val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

        val ouvertData_bProduitDataBase_SubClassFunctionality = bProduitDataBase_SubClassFunctionality.ouvertData


        fun getKeyID(produitID: Long,index:Int): String {
            return createCouleurOnVentKey(
                compt = zAppComptRepositoryComposable.ouvertData!!,
                bProduitDataBase = bProduitDataBase_SubClassFunctionality
                    .datasValue.find { it.id== produitID }!!,
                indexCouleur = index)
        }

        fun getRelatedFAchatCouleurOperation(produitID: Long, index:Int): FCouleurVentOperation? {
            val fAchatCouleurOperation= fCouleurAchatOperationRepositoryComposable
                .datasValue.find { it.keyID == getKeyID(produitID,index) }

            return  fAchatCouleurOperation
        }

        fun ouvreAddDataDepuitIndexCouleur(
            article: ArticlesBasesStatsTable,
            index: Int
        ): Unit {
          /*  val data = dCouleurAchatOperationSubClassFunctionality.getDataDepuitIndex(
                transactionCommercialState.ouvertData!!,
                article,
                nomImageFichieOuApellationDuCouleur= trouve_nomImageFichieOuApellationDuCouleurPar(
                    index,
                    article
                )
            )

            fCouleurAchatOperationRepositoryComposable.addOrUpdateData(data)

            dCouleurAchatOperationSubClassFunctionality.confirmeOldOuvertData(
                ouvertData_dCouleurAchatOperation_SubClassFunctionality
            )?.let {
                fCouleurAchatOperationRepositoryComposable.addOrUpdateData(
                    it
                )
            }

            zAppComptRepositoryComposable
                .subClassFunctionality
                .ouvrireCouleurAchatOperationPourCeCompt(
                    data.key,
                    "${ouvertData_bProduitDataBase_SubClassFunctionality?.nom}_${data.nomImageFichieOuApellationDuCouleur}"
                )        */
        }


        val filteredA_ProduitsParCatalogueBsonId by derivedStateOf {
            bProduitDataBase_SubClassFunctionality.datasValue.filteredParCatalogueBsonId()
        }

        fun List<ArticlesBasesStatsTable>.filteredParCatalogueBsonId(): List<ArticlesBasesStatsTable> {
            val catalogueFilterId =
                zAppComptRepositoryComposable.ouvertData?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId

            val catalogues = B4CatalogueCategoriesRepository().associateBy { it.key }
            val targetCatalogue = catalogues[catalogueFilterId] ?: return this

            val categoriesInCatalogue = b3CategoriesCompoRepository.datasValue
                .filter { it.catalogueParentId == targetCatalogue.id }
                .map { it.id }

            return this.filter { product ->
                val categoryId = product.idParentCategorie
                categoryId != null && categoriesInCatalogue.contains(categoryId)
            }
        }

        val nombreClientsOuLeurDernierEtateCible: Int by derivedStateOf {
            clientsState.datasValue.count { client ->
                val lastTransaction = transactionCommercialState.getClientLastTransaction(client.id)
                lastTransaction?.etateActuellementEst in listOf(
                    C3_TransactionCommercial.EtateActuellementEst.Cible,
                )
            }
        }

        val clientOuSonMarqueMapEstOuvert by derivedStateOf {
            clientsState.findClientById(comptAppState.idClientOuSonMarqueMapEstOuvert)
        }

        val ouvertTransactionCommercial: C3_TransactionCommercial? by derivedStateOf {
            clientOuSonMarqueMapEstOuvert?.let {
                transactionCommercialState.getClientLastTransactionParEtate(
                    it.id, C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                )
            }
        }

        init {
            composScope.launch {
                try {
                    databaseInitializationManager.initializeAllRepositories(context)
                } catch (e: Exception) {
                    databaseInitializationManager.updateMainInitDataBaseProgressEtate(1.0f)
                }
            }

            composScope.launch {
                a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                    masterModel?.let { model ->
                        _loadingProgress.floatValue = model.progress
                    }
                }
            }
        }
        companion object {
            fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()

            // Version that returns Result for better error handling
            fun String?.withOutInvalidCharactersResult(): Result<String> {
                return try {
                    val result = this.withOutInvalidCharacters()
                    Result.success(result)
                } catch (e: IllegalArgumentException) {
                    Result.failure(e)
                }
            }

            fun String?.withOutInvalidCharacters(): String {
                val cleanedNom = (this ?: "")
                    .replace(Regex("[.#\$\\[\\]/®™©{}\"'`~!@%^&*()+=|\\\\:;<>?-]"), "")
                    .replace(Regex("\\s+"), "_")
                    .replace(Regex("_+"), "_")
                    .trim('_')
                    .take(40)


                return when {
                    cleanedNom.isNotEmpty() -> cleanedNom
                    else -> throw IllegalArgumentException("Invalid ID or name")
                }
            }

            fun createCouleurOnVentKey(
                compt: Z_AppCompt,
                bProduitDataBase: ArticlesBasesStatsTable,
                indexCouleur: Int,
            ): String {
                return compt.ouvertF1PeriodVentId +
                        "--${compt.ouvertF2BonVentId}" +
                        "--${bProduitDataBase.id}" +
                        "--${bProduitDataBase.id}_${indexCouleur + 1}"
            }
        }
    }
