package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models

import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.storage.FirebaseStorage
import kotlin.random.Random

@Entity
data class MessageVocale(
    @PrimaryKey(autoGenerate = true)
    val vid: Long=0,
    val keyID: String = "",

    //Infos De Base

    var currentTimeStr: String = DatesHandler().getDateAndTimString().time,
    val vocaleKeyID: String = "",

    //Etates Mutable

) {


    // Test instance function with random value implementation
    companion object {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("2_MessageVocale")


        fun createTestInstance(): MessageVocale {
            // Generate a random number between 1 and 3
            val randomNumber = Random.nextInt(1, 4) // Generates 1, 2, or 3

            return MessageVocale(
                vid = System.currentTimeMillis(),
                vocaleKeyID = "test_${randomNumber}_${System.currentTimeMillis()}",
            )
        }
    }
}
