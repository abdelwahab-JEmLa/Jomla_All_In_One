package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.f.Produit_Vent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Dialog_Fast_Affiche_Panie(modifier: Modifier = Modifier) {
    MainList(modifier = modifier)
}

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val active_Central_Values by remember { focusedValuesGetter::active_Central_Values }

    val groupedVents by remember(active_Central_Values.activeFilters) {
        derivedStateOf {
            val allVents = focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            val activeFilters = active_Central_Values.activeFilters

            val sortVentsParClassement = active_Central_Values.sortVentsParClassment

            val filteredData = when {
                activeFilters.isEmpty() -> allVents

                else -> {
                    allVents.filter { vent ->
                        var passesAllFilters = true

                        activeFilters.forEach { filter ->
                            val passesThisFilter = when (filter) {
                                is ActiveCentralValues.ActiveFilter.NonTrouve -> {
                                    vent.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve
                                }
                                is ActiveCentralValues.ActiveFilter.PrixAuGerant -> {
                                    true
                                }
                            }

                            if (!passesThisFilter) {
                                passesAllFilters = false
                            }
                        }

                        passesAllFilters
                    }
                }
            }

            val groupedData = filteredData
                .groupBy { it.parent_M1Produit_KeyId }
                .mapValues { (_, ventList) ->
                    ventList.sortedByDescending { it.creationTimestamps }
                }
                .toList()

            val sortedData = if (sortVentsParClassement) {
                groupedData.sortedWith(compareBy<Pair<String, List<M10OperationVentCouleur>>> { (produitKeyId, _) ->
                    val produit = aCentralFacade.repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
                    produit?.position_store_3jamale ?: Int.MAX_VALUE
                }.thenByDescending { (produitKeyId, _) ->
                    val produit = aCentralFacade.repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
                    produit?.dernier_timeTamps_position_store_3jamale ?: 0L
                })
            } else {
                groupedData.sortedByDescending { (_, ventList) ->
                    ventList.maxOfOrNull { vent ->
                        if (vent.creationTimestamps > 0) {
                            vent.creationTimestamps
                        } else {
                            vent.dernierTimeTampsSynchronisationAvecFireBase
                        }
                    } ?: 0L
                }
            }

            sortedData.toMap()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(groupedVents.toList()) { (produitKeyId, ventList) ->
            Produit_Vent(
                produitKeyId = produitKeyId,
                ventList = ventList,
                aCentralFacade = aCentralFacade,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
