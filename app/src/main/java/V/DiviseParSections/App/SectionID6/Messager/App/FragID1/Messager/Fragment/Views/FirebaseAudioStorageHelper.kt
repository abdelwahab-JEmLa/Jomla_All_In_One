package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import android.content.Context
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class FirebaseAudioStorageHelper {
    
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val audioRef = storageRef.child("audio_messages").child("D_EtateMessageVocale")
    
    companion object {
        private const val TAG = "FirebaseAudioStorage"
    }

    suspend fun uploadAudioFile(localFile: File, parentMessageVID: Long): Result<String> {
        return try {
            if (!localFile.exists()) {
                return Result.failure(Exception("Local file does not exist: ${localFile.absolutePath}"))
            }
            
            val fileName = "voice_${parentMessageVID}.3gp"
            val audioFileRef = audioRef.child(fileName)

            val uploadTask = audioFileRef.putFile(android.net.Uri.fromFile(localFile))
            uploadTask.await()
            
            val downloadUrl = audioFileRef.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadAudioFile(context: Context, parentMessageVID: Long): Result<File> {
        return try {
            val fileName = "voice_${parentMessageVID}.3gp"
            val audioFileRef = audioRef.child(fileName)
            val localFile = File(context.filesDir, fileName)
            

            audioFileRef.getFile(localFile).await()
            
            if (localFile.exists() && localFile.length() > 0) {
                Result.success(localFile)
            } else {
                Result.failure(Exception("Downloaded file is empty or doesn't exist"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to download audio file for message ID: $parentMessageVID", e)
            Result.failure(e)
        }
    }
}
