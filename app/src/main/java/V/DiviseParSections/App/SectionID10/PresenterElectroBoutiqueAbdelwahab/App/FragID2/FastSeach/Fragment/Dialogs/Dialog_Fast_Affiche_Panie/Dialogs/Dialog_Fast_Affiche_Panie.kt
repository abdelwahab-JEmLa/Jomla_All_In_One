package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent.Produit_Vent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.Home.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Home.SortVentMode
import EntreApps.Shared.Models.M10OperationVentCouleur
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
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

    val groupedVents by remember(
        active_Central_Values.activeFilters,
        active_Central_Values.outlined_filter_searcher_floating_abouve_all,
        active_Central_Values.sortVentMode,
        active_Central_Values.sortVentsParClassment
    ) {
        derivedStateOf {
            val allVents = focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            val activeFilters = active_Central_Values.activeFilters
            val searchQuery = active_Central_Values.outlined_filter_searcher_floating_abouve_all.trim()

            // Determine sort mode - default to PAR_Creation_Vent (Classement)
            val sortMode: SortVentMode = when {
                active_Central_Values.sortVentMode != null -> active_Central_Values.sortVentMode!!
                active_Central_Values.sortVentsParClassment -> SortVentMode.PAR_Creation_Vent
                else -> SortVentMode.PAR_Creation_Vent
            }

            Log.d("Dialog_Fast_Affiche", "=== SORT DEBUG ===")
            Log.d("Dialog_Fast_Affiche", "sortVentMode: ${active_Central_Values.sortVentMode}")
            Log.d("Dialog_Fast_Affiche", "sortVentsParClassment: ${active_Central_Values.sortVentsParClassment}")
            Log.d("Dialog_Fast_Affiche", "Effective sortMode: $sortMode")
            Log.d("Dialog_Fast_Affiche", "Total vents before filter: ${allVents.size}")

            // Check timestamp values
            if (allVents.isNotEmpty()) {
                Log.d("Dialog_Fast_Affiche", "=== TIMESTAMP ANALYSIS ===")
                allVents.take(5).forEachIndexed { index, vent ->
                    Log.d("Dialog_Fast_Affiche", "Vent ${index + 1}: keyID=${vent.keyID.take(8)}..., creationTimestamps=${vent.creationTimestamps}, produit=${vent.parent_M1Produit_DebugInfos}")
                }
                val ventsWithZeroTimestamp = allVents.count { it.creationTimestamps == 0L }
                val ventsWithNonZeroTimestamp = allVents.count { it.creationTimestamps != 0L }
                Log.d("Dialog_Fast_Affiche", "Vents with timestamp=0: $ventsWithZeroTimestamp")
                Log.d("Dialog_Fast_Affiche", "Vents with timestamp>0: $ventsWithNonZeroTimestamp")

                if (ventsWithZeroTimestamp > 0) {
                    Log.w("Dialog_Fast_Affiche", "⚠️ WARNING: $ventsWithZeroTimestamp vents have creationTimestamps=0")
                    Log.w("Dialog_Fast_Affiche", "⚠️ These vents were likely created without setting the timestamp")
                    Log.w("Dialog_Fast_Affiche", "⚠️ Sort by Classement will not work properly until timestamps are fixed")
                }
            }

            val filteredData = when {
                activeFilters.isEmpty() && searchQuery.isEmpty() -> allVents

                else -> {
                    allVents.filter { vent ->
                        var passesAllFilters = true

                        activeFilters.forEach { filter ->
                            val passesThisFilter = when (filter) {
                                is ActiveCentralValues.ActiveFilter.NonTrouve -> {
                                    vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve
                                }

                                is ActiveCentralValues.ActiveFilter.PrixAuGerant -> {
                                    val tariff = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
                                        .find { it.keyID == vent.parentM13TarificationKeyID }

                                    tariff?.laisse_Au_Gerant == true
                                }

                                is ActiveCentralValues.ActiveFilter.premier_Check_Donne -> {
                                    vent.premier_Check_Donne
                                }

                                is ActiveCentralValues.ActiveFilter.non_premier_Check_Donne -> {
                                    !vent.premier_Check_Donne
                                }
                            }

                            if (!passesThisFilter) {
                                passesAllFilters = false
                            }
                        }

                        if (passesAllFilters && searchQuery.isNotEmpty()) {
                            val produit = aCentralFacade.repositorysMainGetter.find_M1Produit_ByKeyID(vent.parent_M1Produit_KeyId)
                            val matchesName = produit?.nom?.contains(searchQuery, ignoreCase = true) == true
                            val matchesArabName = produit?.nomArab?.contains(searchQuery, ignoreCase = true) == true
                            val matchesDebugInfo = vent.parent_M1Produit_DebugInfos.contains(searchQuery, ignoreCase = true)

                            passesAllFilters = matchesName || matchesArabName || matchesDebugInfo
                        }

                        passesAllFilters
                    }
                }
            }

            Log.d("Dialog_Fast_Affiche", "Total vents after filter: ${filteredData.size}")

            val groupedData = filteredData
                .groupBy { it.parent_M1Produit_KeyId }
                .mapValues { (_, ventList) ->
                    ventList.sortedByDescending { it.creationTimestamps }
                }
                .toList()

            Log.d("Dialog_Fast_Affiche", "Total product groups: ${groupedData.size}")

            val sortedData = when (sortMode) {
                SortVentMode.PAR_Creation_Vent -> {
                    // Sort by creation timestamp (most recent first = "Classement")
                    groupedData.sortedWith(compareByDescending<Pair<String, List<M10OperationVentCouleur>>> { (_, ventList) ->
                        ventList.maxOfOrNull { vent -> vent.creationTimestamps } ?: 0L
                    }.thenBy { (produitKeyId, _) ->
                        val produit = aCentralFacade.repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
                        produit?.nom?.lowercase() ?: ""
                    }).also { sorted ->
                        Log.d("Dialog_Fast_Affiche", "PAR_Creation_Vent (Classement) - First 5 products:")
                        sorted.take(5).forEachIndexed { index, (produitKeyId, ventList) ->
                            val produit = aCentralFacade.repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
                            val maxTimestamp = ventList.maxOfOrNull { it.creationTimestamps } ?: 0L
                            Log.d("Dialog_Fast_Affiche", "  ${index + 1}. ${produit?.nom} - timestamp: $maxTimestamp (${ventList.size} vents)")
                        }
                    }
                }

                SortVentMode.PAR_ENTREE -> {
                    // Sort alphabetically by product name (A-Z = "Entrée")
                    groupedData.sortedWith(compareBy<Pair<String, List<M10OperationVentCouleur>>> { (produitKeyId, _) ->
                        val produit = aCentralFacade.repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
                        produit?.nom?.lowercase() ?: ""
                    }.thenBy { (produitKeyId, _) ->
                        val produit = aCentralFacade.repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
                        produit?.nomArab?.lowercase() ?: ""
                    }.thenByDescending { (_, ventList) ->
                        ventList.maxOfOrNull { vent -> vent.creationTimestamps } ?: 0L
                    }).also {
                        Log.d("Dialog_Fast_Affiche", "PAR_ENTREE (Alphabétique) - Sorted")
                    }
                }

                SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> {
                    // Sort by last update timestamp (most recently checked first = "Dernière Vérification")
                    groupedData.sortedWith(compareByDescending<Pair<String, List<M10OperationVentCouleur>>> { (_, ventList) ->
                        ventList.maxOfOrNull { vent ->
                            if (vent.last_update_premier_Check_Donne_TimeTamps != 0L) {
                                vent.last_update_premier_Check_Donne_TimeTamps
                            } else {
                                vent.creationTimestamps
                            }
                        } ?: 0L
                    }.thenBy { (produitKeyId, _) ->
                        val produit = aCentralFacade.repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
                        produit?.nom?.lowercase() ?: ""
                    }).also { sorted ->
                        Log.d("Dialog_Fast_Affiche", "PAR_DERNIERE_UPDATE_LENCE (Dernière Vérification) - First 5 products:")
                        sorted.take(5).forEachIndexed { index, (produitKeyId, ventList) ->
                            val produit = aCentralFacade.repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
                            val maxTimestamp = ventList.maxOfOrNull { vent ->
                                if (vent.last_update_premier_Check_Donne_TimeTamps != 0L) {
                                    vent.last_update_premier_Check_Donne_TimeTamps
                                } else {
                                    vent.creationTimestamps
                                }
                            } ?: 0L
                            Log.d("Dialog_Fast_Affiche", "  ${index + 1}. ${produit?.nom} - timestamp: $maxTimestamp (${ventList.size} vents)")
                        }
                    }
                }
            }

            sortedData.toMap()
        }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(groupedVents.toList()) { index, (produitKeyId, ventList) ->
            val totalItems = groupedVents.size
            val positionIndex = totalItems - index

            Produit_Vent(
                produitKeyId = produitKeyId,
                ventList = ventList,
                positionIndex = positionIndex,
                aCentralFacade = aCentralFacade,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
