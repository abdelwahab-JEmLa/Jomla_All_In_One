package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import android.content.Context
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

/**
 * Helper class to handle audio file uploads and downloads to/from Firebase Storage
 */
class FirebaseAudioStorageHelper {
    
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val audioRef = storageRef.child("audio_messages")
    
    companion object {
        private const val TAG = "FirebaseAudioStorage"
    }
    
    /**
     * Upload an audio file to Firebase Storage
     * @param localFile The local audio file to upload
     * @param parentMessageVID The message ID to use as filename
     * @return Success/failure result
     */
    suspend fun uploadAudioFile(localFile: File, parentMessageVID: Long): Result<String> {
        return try {
            if (!localFile.exists()) {
                return Result.failure(Exception("Local file does not exist: ${localFile.absolutePath}"))
            }
            
            val fileName = "voice_${parentMessageVID}.3gp"
            val audioFileRef = audioRef.child(fileName)
            
            Log.d(TAG, "Uploading audio file: $fileName")
            
            // Upload the file
            val uploadTask = audioFileRef.putFile(android.net.Uri.fromFile(localFile))
            uploadTask.await()
            
            // Get the download URL
            val downloadUrl = audioFileRef.downloadUrl.await()
            
            Log.d(TAG, "Successfully uploaded audio file: $fileName")
            Result.success(downloadUrl.toString())
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload audio file", e)
            Result.failure(e)
        }
    }
    
    /**
     * Download an audio file from Firebase Storage
     * @param context Android context
     * @param parentMessageVID The message ID to download
     * @return The local file if successful
     */
    suspend fun downloadAudioFile(context: Context, parentMessageVID: Long): Result<File> {
        return try {
            val fileName = "voice_${parentMessageVID}.3gp"
            val audioFileRef = audioRef.child(fileName)
            val localFile = File(context.filesDir, fileName)
            
            Log.d(TAG, "Downloading audio file: $fileName")
            
            // Download the file
            audioFileRef.getFile(localFile).await()
            
            if (localFile.exists() && localFile.length() > 0) {
                Log.d(TAG, "Successfully downloaded audio file: $fileName")
                Result.success(localFile)
            } else {
                Result.failure(Exception("Downloaded file is empty or doesn't exist"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to download audio file for message ID: $parentMessageVID", e)
            Result.failure(e)
        }
    }
    
    /**
     * Check if an audio file exists in Firebase Storage
     * @param parentMessageVID The message ID to check
     * @return True if file exists, false otherwise
     */
    suspend fun audioFileExists(parentMessageVID: Long): Boolean {
        return try {
            val fileName = "voice_${parentMessageVID}.3gp"
            val audioFileRef = audioRef.child(fileName)
            
            // Try to get metadata - this will throw an exception if file doesn't exist
            audioFileRef.metadata.await()
            true
        } catch (e: Exception) {
            Log.d(TAG, "Audio file does not exist for message ID: $parentMessageVID")
            false
        }
    }
    
    /**
     * Delete an audio file from Firebase Storage
     * @param parentMessageVID The message ID to delete
     * @return Success/failure result
     */
    suspend fun deleteAudioFile(parentMessageVID: Long): Result<Unit> {
        return try {
            val fileName = "voice_${parentMessageVID}.3gp"
            val audioFileRef = audioRef.child(fileName)
            
            audioFileRef.delete().await()
            Log.d(TAG, "Successfully deleted audio file: $fileName")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete audio file for message ID: $parentMessageVID", e)
            Result.failure(e)
        }
    }
}
