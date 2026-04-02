package Application2.App.Base.Repository

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Stable
class RepositorysMainGetter_app2(
) {
    val repoScope = CoroutineScope(Dispatchers.IO)
    private val _ActiveCentralValues_app2 = mutableStateOf(
        ActiveCentralValues_app2()
    )
    val active_Central_Values by derivedStateOf { _ActiveCentralValues_app2.value }

    val loadingProgress: Float by derivedStateOf {
        active_Central_Values.mainInitDataBaseProgressEtate
    }

    fun update_ActiveCentralValues_app2(new: ActiveCentralValues_app2) {
        _ActiveCentralValues_app2.value = new
    }
}
