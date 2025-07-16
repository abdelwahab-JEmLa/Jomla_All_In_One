package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.Repo3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo18P.Repository.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier

data class ActiveCentralValues(
    val m17Message_avec_BonVen: M17MessageVocale? =null,
) {
    companion object{
        fun get_Default(): ActiveCentralValues {
           return ActiveCentralValues()
        }
    }
}

@Stable
class FocusedValuesGetter(
    repo2Client: Repo2Client,
    repoM1ProduitInfos: RepoM1Produit,
    repo3CouleurProduitInfos: Repo3CouleurProduitInfos,

    repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    private val repo13TarificationInfos: Repo13TarificationInfos,
    private val repo14VentPeriode: Repo14VentPeriode,
) {
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private val _activeCentralValues = mutableStateOf(ActiveCentralValues())
    val active_Central_Values by derivedStateOf { _activeCentralValues.value}

    fun update_activeCentralValues(new: ActiveCentralValues): Unit {
        _activeCentralValues.value= new
    }
    val active_Current_M9AppCompt by derivedStateOf {
        repo9AppCompt.datasValue.firstOrNull { it.keyID == ParametresAppComptNonSaved().currentActiveFocucedM9AppComptKeyID }
    }

    val currentActiveFocuced_M14VentPeriode by derivedStateOf {
        repo14VentPeriode.datasValue.find { it.keyID == active_Current_M9AppCompt?.current_OnVent_M14VentPeriode_KeyID }
            ?: repo14VentPeriode.datasValue.lastOrNull()
    }

    //----------------------------------Section.M8BonVent------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    val filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod by derivedStateOf {
        repo8BonVent.datasValue.filter { it.parent_M14VentPeriod_KeyId == currentActiveFocuced_M14VentPeriode?.keyID }
    }

    val activeonVent_M8BonVent by derivedStateOf {
        repo8BonVent.datasValue.find { it.keyID == active_Current_M9AppCompt?.onVentM8BonVentKey }
    }

    //----------------------------------Section.M2Client------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    val filteredList_M2Client_Ou_Leur_Last_M8BonVent_Etate_IS_Cible by derivedStateOf {
        repo2Client.datasValue.filter { client ->
            filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
                .sortedBy { it.creationTimestamps }
                .lastOrNull { it.parent_M2Client_KeyID == client.keyID }
                ?.etateActuellementEst == M8BonVent.EtateActuellementEst.Cible
        }
    }
    //----------------------------------Section.M10Vent------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    val filtered_ListM10Vent_BY_Curr_M14VentPeriod by derivedStateOf {
        repo10OperationVentCouleur.datasValue.filter {
            val parent_M9AppCompt =
                repo9AppCompt.datasValue.find { compt ->
                    compt.keyID == it.parent_M9AppCompt_KeyID
                }

            it.parent_M14VentPeriod_KeyId ==currentActiveFocuced_M14VentPeriode?.keyID
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    val filteredList_M2Client_LastM8BonVentEtate_IS_ON_MODE_COMMEND_ACTUELLEMENT by derivedStateOf {
        repo2Client.datasValue.filter { client ->
            val lastBonVent = filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
                .filter {
                    (it.parent_M2Client_KeyID == client.keyID
                            && it.parent_M9AppCompt_KeyID == (active_Current_M9AppCompt?.keyID ?: ""))
                }
                .maxByOrNull { it.creationTimestamps }

            lastBonVent?.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    val activeOnVentM2ClientInfos by derivedStateOf {
        repo2Client.datasValue.find {
            it.keyID == (activeonVent_M8BonVent?.parent_M2Client_KeyID ?: "")
        }
    }

    fun getDefaultM8BonVent(): M8BonVent {
        return M8BonVent(
            keyID = M8BonVent.generePushKey(),
            parent_M9AppCompt_KeyID = ParametresAppComptNonSaved().currentActiveFocucedM9AppComptKeyID,
            parent_M9AppCompt_DebugInfos = ParametresAppComptNonSaved().currentActiveFocucedM9AppComptDebugInfos,
            parent_M14VentPeriod_KeyId = (currentActiveFocuced_M14VentPeriode?.keyID ?: "null"),
            parent_M14VentPeriod_DebugInfos = (currentActiveFocuced_M14VentPeriode?.get_DebugInfos()
                ?: "null"),
        )
    }

    val activeOnVent_M2Client by derivedStateOf {
        val targetKey = activeonVent_M8BonVent?.parent_M2Client_KeyID
        repo2Client.datasValue.find { it.keyID == targetKey }
    }

    fun getDefaultM10VentOperation(): M10OperationVentCouleur? {
        return activeonVent_M8BonVent?.let {
            with(it) {
                M10OperationVentCouleur(
                    //---------------------------------Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
                    parent_M14VentPeriod_KeyId = parent_M14VentPeriod_KeyId,
                    parent_M14VentPeriod_DebugInfos = parent_M14VentPeriod_DebugInfos,
                    //---------------------------------Parent M8BonVent----------------------------------------------------------------------------------------------------------------------------------
                    parentM8BonVentKeyId = keyID,
                    parentM8BonVentDebugInfos = get_DebugInfos(),

                )
            }
        }

    }

    val onVentM10VentOperation by derivedStateOf {
        repo10OperationVentCouleur.datasValue.find { it.keyID == active_Current_M9AppCompt?.onVentM3CouleurProduitInfosKeyID }
    }

    val onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent by derivedStateOf {
        repo10OperationVentCouleur.datasValue.filter {
            it.parentM8BonVentKeyId == (active_Current_M9AppCompt?.onVentM8BonVentKey ?: "")
        }
    }

    val focused_M1ProduitInfos_Pour_PrixDifineur by derivedStateOf {
        repoM1ProduitInfos.datasValue.find { it.keyID == active_Current_M9AppCompt?.activeFocuce_TariffPrixDifineur_M1ProduitKeyID }
    }
    //---------------------------------Parent m10OperationVentCouleurs----------------------------------------------------------------------------------------------------------------------------------

    fun get_ListFiltered_M10OperationVentCouleurs_By_M1Produit(
        produit: ArticlesBasesStatsTable
    ) = onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { ventOperation ->
            ventOperation.parentM1ProduitInfosKeyId == produit.keyID
        }

    val focused_ListM10OpeVentCouleur_Par_PD_M1Produit by derivedStateOf {
        onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.filter {
            it.parentM1ProduitInfosKeyId == (focused_M1ProduitInfos_Pour_PrixDifineur?.keyID ?: "")
        }
    }

    //---------------------------------m10OperationVentCouleurs----------------------------------------------------------------------------------------------------------------------------------

    val active_M1ProduitInfos_In_CurCompt_DialogQantity_Defineur by derivedStateOf {
        repoM1ProduitInfos.datasValue.find {
            it.keyID == (active_Current_M9AppCompt?.dialogChoisireQuantityM1ProduitInfosKeyID ?: "")
        }
    }

    val focused_M13TarificationInfos_Pour_Produit by derivedStateOf {
        repo13TarificationInfos.datasValue.lastOrNull { tariff ->
            tariff.typeChoisi == TypeChoisi.DefiniParGerant &&
                    tariff.parent_M1Produit_KeyId == (focused_M1ProduitInfos_Pour_PrixDifineur?.keyID ?: "")
        }
    }

    val activeDialogSearchM1Produit by derivedStateOf {
        active_Current_M9AppCompt?.activeDialogSearchM1Produit ?: false
    }

    companion object {
        @SuppressLint("ModifierFactoryUnreferencedReceiver")
        fun Modifier.getSemanticsTagFocucedVars(getter: FocusedValuesGetter): Modifier {
            val map = buildMap {
                with(getter) {
                    put(
                        "onVentM2ClientInfos",
                        activeOnVent_M2Client?.let {
                            with(it) {
                                nom
                            }
                        } ?: "null"
                    )
                    put(
                        "onVentM8BonVent",
                        activeonVent_M8BonVent?.let {
                            with(it) {
                                "$parent_M2Client_DebugInfos/$etateActuellementEst"
                            }
                        } ?: "null"
                    )
                    put(
                        "focused_M1ProduitInfos_Pour_PrixDifineur", getter
                            .focused_M1ProduitInfos_Pour_PrixDifineur?.let {
                                it.nom + it.keyID
                            } ?: "null")

                    put(
                        "onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent",
                        getter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.map {
                            "${it.parentM1ProduitDebugInfos} / ${it.parentM1ProduitInfosKeyId}"
                        }
                    )
                    put(
                        "focused_ListM10OpeVentCouleur_Par_PD_M1Produit",
                        getter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit.map { it.getDebugInfos() }
                    )
                }
            }

            return map.entries.foldIndexed(this) { index, modifier, (key, value) ->
                modifier.getSemanticsTag_By_datas_A_Affiche_Au_Nom(index + 6, key, value)
            }
        }
    }
}

fun get_By_Client_Edited_M8BonVent_Et_M9CurrCompt(
    m2Client: M2Client,
    defaultM8BonVent: M8BonVent,
    onVentM8BonVent: M8BonVent?,
    currentM9AppCompt: Z_AppCompt?,
    newEtate: M8BonVent.EtateActuellementEst,
): Pair<M8BonVent, Z_AppCompt?> {
    val onVentM8BonVentWithDefault = onVentM8BonVent ?: defaultM8BonVent

    val editedM8BonVent = onVentM8BonVentWithDefault.copy(
        parent_M2Client_KeyID = m2Client.keyID,
        parent_M2Client_DebugInfos = m2Client.nom,
        etateActuellementEst = newEtate
    )

    val editedM9CurrCompt = currentM9AppCompt?.copy(
        onVentM8BonVentKey = editedM8BonVent.keyID,
        onVentM8BonVentDebugInfos = editedM8BonVent.get_DebugInfos()
    )
    return Pair(editedM8BonVent, editedM9CurrCompt)
}
