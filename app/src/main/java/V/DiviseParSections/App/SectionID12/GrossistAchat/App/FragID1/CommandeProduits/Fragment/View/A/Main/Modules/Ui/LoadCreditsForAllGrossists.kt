package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A.loadCreditForGrossist
import EntreApps.Shared.Models.Relative_Vents.Models.M15Grossist
import com.google.firebase.database.ValueEventListener

// Helper function to load credits for all grossists with real-time updates
 fun loadCreditsForAllGrossists(
    grossists: List<M15Grossist>,
    onCreditsLoaded: (Map<String, Double>) -> Unit
): Pair<Map<String, List<ValueEventListener>>, Map<String, Double>> {
    val creditsMap = mutableMapOf<String, Double>()
    val allListeners = mutableMapOf<String, List<ValueEventListener>>()

    if (grossists.isEmpty()) {
        onCreditsLoaded(emptyMap())
        return Pair(emptyMap(), emptyMap())
    }

    grossists.forEach { grossist ->
        val listeners = loadCreditForGrossist(grossist.keyID) { credit ->
            creditsMap[grossist.keyID] = credit
            onCreditsLoaded(creditsMap.toMap())
        }
        allListeners[grossist.keyID] = listeners
    }

    return Pair(allListeners, creditsMap.toMap())
}
