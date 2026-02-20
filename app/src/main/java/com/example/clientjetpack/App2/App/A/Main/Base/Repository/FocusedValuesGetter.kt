// FocusedValuesGetter_app2.kt - FIXED VERSION
package com.example.clientjetpack.App2.App.A.Main.Base.Repository

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


@Stable
class FocusedValuesGetter_app2() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var temporaryModeJob: Job? = null

    private val _ActiveCentralValues_app2 = mutableStateOf(
        ActiveCentralValues_app2()
    )
    val active_Central_Values by derivedStateOf { _ActiveCentralValues_app2.value }

    fun update_ActiveCentralValues_app2(new: ActiveCentralValues_app2) {
        _ActiveCentralValues_app2.value = new
    }
}

