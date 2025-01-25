package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils

import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.ViewModelExtensionMapsHandler
import com.example.clientjetpack.Models.AppSettingsSaverModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

fun ViewModelExtensionMapsHandler.updateLongAppSetting(
    value: Long,
    name: String = "clientBuyerNowId",
) {
    viewModelScope.launch {
        try {
            val appSettingsSaverModel = AppSettingsSaverModel(
                id = 1,
                name = name,
                valueLong = value,
                date = Date()
            )

            Firebase.database.getReference("A_AppSettingsSaverModel")
                .child(appSettingsSaverModel.id.toString())
                .setValue(appSettingsSaverModel)
                .await()
        } catch (e: Exception) {
        }
    }
}
