package com.example.clientjetpack.ViewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * Singleton manager to control the visibility of the control buttons across the app
 */
class ControlButtonsVisibilityManager {
    // Visibility state of the control buttons
    val isVisible: MutableState<Boolean> = mutableStateOf(true)
    
    // Method to hide buttons (call this before showing dialogs)
    fun hideButtons() {
        isVisible.value = false
    }
    
    // Method to show buttons (call this after dismissing dialogs)
    fun showButtons() {
        isVisible.value = true
    }
}
