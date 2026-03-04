package EntreApps.Shared.Models.Home

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Job
    @Stable
class FocusedValues_FluidApp(
) {
    private var temporaryModeJob: Job? = null

    private val _activeCentralValues = mutableStateOf(
        ActiveCentralValues()
    )

    val active_Central_Values by derivedStateOf { _activeCentralValues.value }

}
