package Z_CodePartageEntreApps.Modules

import android.content.Context
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.net.URLDecoder

class A_FirebaseAudioStorageHelper {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val audioRef = storageRef.child("audio_messages").child("M17MessageVocale")

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

    // New method to download audio file using the Firebase URL directly
    suspend fun downloadAudioFileFromUrl(context: Context, firebaseUrl: String): Result<File> {
        return try {
            Log.d(TAG, "Downloading audio from URL: $firebaseUrl")

            // Extract filename from Firebase URL
            val fileName = extractFileNameFromUrl(firebaseUrl)
            Log.d(TAG, "Extracted filename: $fileName")

            val localFile = File(context.filesDir, fileName)

            // Check if file already exists locally
            if (localFile.exists() && localFile.length() > 0) {
                Log.d(TAG, "File already exists locally: ${localFile.absolutePath}")
                return Result.success(localFile)
            }

            // Download from Firebase using the URL reference
            val audioFileRef = storage.getReferenceFromUrl(firebaseUrl)
            audioFileRef.getFile(localFile).await()

            if (localFile.exists() && localFile.length() > 0) {
                Log.d(TAG, "Successfully downloaded file: ${localFile.absolutePath}, size: ${localFile.length()}")
                Result.success(localFile)
            } else {
                Result.failure(Exception("Downloaded file is empty or doesn't exist"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to download audio file from URL: $firebaseUrl", e)
            Result.failure(e)
        }
    }

    private fun extractFileNameFromUrl(firebaseUrl: String): String {
        return try {
            // Firebase Storage URLs have the format:
            // https://firebasestorage.googleapis.com/v0/b/bucket/o/path%2Ffilename?alt=media&token=...
            // We need to extract the filename from the path

            val decodedUrl = URLDecoder.decode(firebaseUrl, "UTF-8")
            Log.d(TAG, "Decoded URL: $decodedUrl")

            // Look for the pattern "voice_XXX.3gp" in the URL
            val regex = Regex("voice_\\d+\\.3gp")
            val matchResult = regex.find(decodedUrl)

            if (matchResult != null) {
                val fileName = matchResult.value
                Log.d(TAG, "Found filename using regex: $fileName")
                return fileName
            }

            // Fallback: try to extract from the 'o/' part of the URL
            val pathStart = decodedUrl.indexOf("/o/")
            val pathEnd = decodedUrl.indexOf("?", pathStart)

            if (pathStart != -1 && pathEnd != -1) {
                val fullPath = decodedUrl.substring(pathStart + 3, pathEnd)
                val fileName = fullPath.substringAfterLast("/")
                Log.d(TAG, "Extracted filename from path: $fileName")
                return fileName
            }

            // Last resort fallback
            Log.w(TAG, "Could not extract filename from URL, using default")
            "voice_unknown.3gp"

        } catch (e: Exception) {
            Log.e(TAG, "Error extracting filename from URL: $firebaseUrl", e)
            "voice_unknown.3gp"
        }
    }
}
