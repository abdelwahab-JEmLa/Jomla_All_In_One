package com.example.clientjetpack.Models

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class ColorArrangementWrapper(
    @SerializedName("NewArregmentColorsJsonStruct")
    val NewArregmentColorsJsonStruct: List<ColorArrangement>
)

data class ColorArrangement(
    @SerializedName("idColore")
    val idColore: Long,
    @SerializedName("colorSoldQuantity")
    val colorSoldQuantity: Int = 0
)

data class ProductDisplayController(
    val newArregmentColorsJsonStruct: String = "",
    val clientWindowsDisplayedProductId: Long? = null,
    val searchWindowsDisplaye: String = "",
    val clientWindowsPickerDisplayedQuantity: Int = 0,
    val clientWindowsSelectedColorId: Long = 0,
    val clientWindowsLazyRowSupColorsScroll: Int = 0,
    val mainGridScrollPosition: Int = 0,
    val isConnected: Boolean = false,
    val connectionStatus: String = "Déconnecté",
    val isHostPhone: Boolean = true,
    val switchRoles: Boolean = true,
    val testMessageByWifi: String = "",
    val error: String? = null
) {
    private val gson = Gson()

    fun getColorArrangement(): List<ColorArrangement> {
        return try {
            if (newArregmentColorsJsonStruct.isEmpty()) {
                Log.d("ProductDisplayController", "Empty JSON string")
                return emptyList()
            }

            // Log the JSON for debugging
            Log.d("ProductDisplayController", "Parsing JSON: $newArregmentColorsJsonStruct")

            // Parse using Gson
            val wrapper = gson.fromJson(newArregmentColorsJsonStruct, ColorArrangementWrapper::class.java)
            Log.d("ProductDisplayController", "Successfully parsed ${wrapper.NewArregmentColorsJsonStruct.size} colors")

            wrapper.NewArregmentColorsJsonStruct
        } catch (e: Exception) {
            Log.e("ProductDisplayController", "Error parsing JSON: ${e.message}")
            Log.e("ProductDisplayController", "Stack trace:", e)
            emptyList()
        }
    }
}
