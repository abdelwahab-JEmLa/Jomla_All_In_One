package EntreApps.Shared.Models

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Entity
data class M10OperationVentCouleur(
    @PrimaryKey var keyID: String = RepositorysMainGetter.Companion.getPushFireBase(ref),
    var creationTimestamps: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    var its_created_in_working_for_wholesaler: Boolean = false,

    var commetaire: String = "",

    //---------------------------------LinkedVent----------------------------------------------------------------------------------------------------------------------------------
    var its_Linked_To_Autre_Vent_Si_NonDispo: Boolean = false,
    val linked_To_M10OperationVent_KeyID: String = "",
    val linked_To_M10OperationVent_DebugInfos: String = "",
    //---------------------------------LinkedVent.FastInfos----------------------------------------------------------------------------------------------------------------------------------
    val siNonDispoParentM10Vent_it_parent_M3CouleurInfos_KeyId: String = "",
    val siNonDispoParentM10Vent_it_parent_M1Produit_Nom: String = "",

    //---------------------------------M9AppCompt----------------------------------------------------------------------------------------------------------------------------------
    val parent_M9AppCompt_KeyID: String = "null",
    val parent_M9AppCompt_DebugInfos: String = "null",
    //---------------------------------Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parent_M14VentPeriod_KeyId: String = "null",
    var parent_M14VentPeriod_DebugInfos: String = "null",
    var parentEPeriodVentStartDate: Long = 0,
    //---------------------------------Parent M8BonVent----------------------------------------------------------------------------------------------------------------------------------
    var parent_M8BonVent_KeyId: String = "null",
    val parent_M8BonVent_DebugInfos: String = "null",
    //---------------------------------Parent M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parent_M1Produit_KeyId: String = "null",
    val parent_M1Produit_DebugInfos: String = "null",
    //---------------------------------New.Proto----------------------------------------------------------------------------------------------------------------------------------
    val parent_M1Produit_Nom: String = "",
    //---------------------------------Old.Proto----------------------------------------------------------------------------------------------------------------------------------
    var parentProduitInfosOldId: Long = 0,

    //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parent_M3CouleurProduit_KeyID: String = "null",
    val parent_M3CouleurProduit_DebugInfos: String = "null",
    //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var parentM13TarificationKeyID: String = "null",
    var parentM13TarificationDebugInfos: String = "null",
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.CreeSlote,

    //Mutable
    var provisoireMonPrix: Double = 0.0,
    var etateDelivery: EtateDelivery = EtateDelivery.Trouve,

    var lence_pour_check: Boolean = false,
    var premier_Check_Donne: Boolean = false,
    var last_update_premier_Check_Donne_TimeTamps: Long = 0L,

    var non_places_au_depot: Boolean = false,
    var pas_Dispo_Pour_Aujourduit: Boolean = false,

    var typeTarificationEnumT2: M13TarificationInfos.TypeChoisi = M13TarificationInfos.TypeChoisi.Prix_Detaille,

    var parentClientInfosKeyID: String = "",
    var parentClientName: String = "",
    var type: Type = Type.CommandeDeLui,
    var achatParentBsonIDOld: String = "",

    val quantite_Boit_Par_Carton: Int = 10,
    var quantity: Int = 0,
    var setIN_Vent_Its_Quantity_Represent: SetIN_Vent_Its_Quantity_Represent =
        SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit,
    var affiche_Unite_Au_Printing: Boolean = true,

    // Adding M2Client reference for proper filtering
    var parent_M2Client_KeyID: String = "null",
) {
    fun getDebugInfos(): String {
        return buildString {
            append("[")
            append("10Vent")
            append("{${keyID.takeLast(4).uppercase()}}\n")
            append(" To ")
            append(parent_M1Produit_DebugInfos)
            append("]")
        }
    }
    enum class SetIN_Vent_Its_Quantity_Represent {
        quantity_Par_Boit,
        quantity_Par_Carton;

        fun toggle(): SetIN_Vent_Its_Quantity_Represent {
            return when (this) {
                quantity_Par_Boit -> quantity_Par_Carton
                quantity_Par_Carton -> quantity_Par_Boit
            }
        }
    }

    fun get_Quantity_Apre_Passe_Au_SetIN_Vent_Its_Quantity_Represent(): Int {
        return if (setIN_Vent_Its_Quantity_Represent ==
            SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton
        )
            quantity / quantite_Boit_Par_Carton
        else
            quantity
    }

    enum class EtateDelivery {
        Trouve,
        NonTrouve
    }

    enum class EtateActuellementEst {
        CreeSlote,
        ParentBonVentOuvert,
        ParentProduitOuvert,
        ChoisiQuantityDialogOuvert,
        ChoisiQuantityConfirme,
        ParentProduitConfirme,
        ParentBonVentConfirme,
        SUPPRIME_AU_PREMIER_PICK,
        SUPP_AU_PANIER_FINALE,
    }

    enum class Type { SiNonDispo, CommandeDeLui }

    companion object {
        fun get_default_By_BonVentEtCouleur(
            onVent_M8BonVent: M8BonVent?,
            m3CouleurProduit: M3CouleurProduitInfos?
        ): M10OperationVentCouleur {
            return M10OperationVentCouleur(
                parent_M9AppCompt_KeyID = onVent_M8BonVent?.parent_M9AppCompt_KeyID ?: "null",
                parent_M9AppCompt_DebugInfos = onVent_M8BonVent?.parent_M9AppCompt_DebugInfos ?: "null",

                parent_M14VentPeriod_KeyId = onVent_M8BonVent?.parent_M14VentPeriod_KeyId ?: "null",
                parent_M14VentPeriod_DebugInfos = onVent_M8BonVent?.parent_M14VentPeriod_DebugInfos ?: "null",

                parent_M8BonVent_KeyId = onVent_M8BonVent?.keyID ?: "null",
                parent_M8BonVent_DebugInfos = onVent_M8BonVent?.get_DebugInfos() ?: "null",

                parent_M1Produit_KeyId = m3CouleurProduit?.parentBProduitInfosKeyID ?: "null",
                parent_M1Produit_DebugInfos = m3CouleurProduit?.parentBProduitInfosKeyID ?: "null",

                parent_M3CouleurProduit_KeyID = m3CouleurProduit?.keyID ?: "null",
                parent_M3CouleurProduit_DebugInfos = m3CouleurProduit?.get_DebugsInfos() ?: "null",

                parent_M2Client_KeyID = onVent_M8BonVent?.parent_M2Client_KeyID ?: "null",
            )
        }

        val ref =
            Firebase.database.getReference("/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Datas10OperationVentCouleur")

        fun remove_ref(){
            ref.removeValue()
        }

        fun isSame(
            ancien: M10OperationVentCouleur,
            newData: M10OperationVentCouleur
        ): Boolean {
            return ancien.keyID == newData.keyID
        }

    }
}
