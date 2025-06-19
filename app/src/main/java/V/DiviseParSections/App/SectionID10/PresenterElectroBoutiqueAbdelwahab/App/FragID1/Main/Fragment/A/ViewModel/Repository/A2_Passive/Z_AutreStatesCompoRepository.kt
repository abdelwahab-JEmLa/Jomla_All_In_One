package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A2_Passive

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Stable
class Z_AutreStatesCompoRepository(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _secteursList = mutableStateOf<List<E1SecteurDeClients>>(emptyList())
    val secteursList: State<List<E1SecteurDeClients>> = _secteursList

    private val _panelsGroupeList = mutableStateOf(
        listOf(
            PanelsGroupeButton(PanelsGroupeButton.Keys.MapSecteursPolygenHandelButtons, false),
            PanelsGroupeButton(PanelsGroupeButton.Keys.autres, false),
        )
    )
    val panelsGroupeList: State<List<PanelsGroupeButton>> = _panelsGroupeList


    fun updateSecteursList(newSecteurs: List<E1SecteurDeClients>) {
        _secteursList.value = newSecteurs
    }

    fun updatePanelsGroupe(newPanels: List<PanelsGroupeButton>) {
        _panelsGroupeList.value = newPanels
    }

    @Stable
    data class PanelsGroupeButton(
        val key: Keys,
        val isVisible: Boolean = false,
    ) {
        enum class Keys {
            MapSecteursPolygenHandelButtons,
            autres,
        }
    }

}


