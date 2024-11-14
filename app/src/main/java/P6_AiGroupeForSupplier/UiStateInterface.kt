package P6_AiGroupeForSupplier

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiStateInterface {

    /**
     * Empty state when the screen is first shown
     */
    data object Initial : UiStateInterface

    /**
     * Still loading
     */
    data object Loading : UiStateInterface

    /**
     * Text has been generated
     */
    data class Success(val outputText: String) : UiStateInterface

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : UiStateInterface
}
