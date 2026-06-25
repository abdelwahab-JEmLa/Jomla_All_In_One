package A.AtelierMobile.Test.ID1.Test.Shared.DataBase.Fonctions.Main

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

fun convertVideoToGif(
    context: Context,
    videoUri: Uri,
    gifFile: File,
    maxDurationMs: Long = 5000,
    fps: Int = 10
): Boolean {
    val retriever = MediaMetadataRetriever()
    var fos: FileOutputStream? = null
    try {
        retriever.setDataSource(context, videoUri)
        
        // Get duration
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationMs = durationStr?.toLongOrNull() ?: 0L
        
        // If duration is missing/zero, default to maxDurationMs to attempt extraction.
        // Otherwise, trim/cut to maxDurationMs if it exceeds it.
        val limitMs = if (durationMs <= 0L) {
            Log.d("VideoToGifConverter", "Video duration metadata missing. Defaulting limit to $maxDurationMs ms.")
            maxDurationMs
        } else if (durationMs > maxDurationMs) {
            Log.d("VideoToGifConverter", "Video duration ($durationMs ms) exceeds limit ($maxDurationMs ms). Trimming video.")
            maxDurationMs
        } else {
            durationMs
        }

        // Calculate size/dimensions
        val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        val videoWidth = widthStr?.toIntOrNull()?.takeIf { it > 0 } ?: 320
        val videoHeight = heightStr?.toIntOrNull()?.takeIf { it > 0 } ?: 240
        
        // Scale dimensions down to reduce file size (max width 320px)
        val targetWidth = 320
        val targetHeight = (videoHeight * (targetWidth.toFloat() / videoWidth.toFloat())).toInt().takeIf { it > 0 } ?: 240

        fos = FileOutputStream(gifFile)
        val encoder = AnimatedGifEncoder(fos)
        encoder.setSize(targetWidth, targetHeight)
        encoder.delay = (1000 / fps).toInt()
        encoder.repeat = 0 // loop infinitely
        encoder.sample = 20 // faster encoding (quality setting 20)
        
        if (!encoder.start()) {
            Log.e("VideoToGifConverter", "Failed to start AnimatedGifEncoder")
            return false
        }

        // Frame interval in microseconds (retriever uses microseconds)
        val intervalUs = 1000000L / fps
        val limitUs = limitMs * 1000L
        
        var currentUs = 0L
        var addedFrames = 0
        var consecutiveNullFrames = 0
        
        while (currentUs < limitUs) {
            val frame: Bitmap? = try {
                retriever.getFrameAtTime(currentUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            } catch (e: Exception) {
                null
            }
            
            if (frame != null) {
                consecutiveNullFrames = 0
                // Resize frame
                val resizedFrame = Bitmap.createScaledBitmap(frame, targetWidth, targetHeight, true)
                encoder.addFrame(resizedFrame)
                if (resizedFrame != frame) {
                    resizedFrame.recycle()
                }
                frame.recycle()
                addedFrames++
            } else {
                consecutiveNullFrames++
                if (consecutiveNullFrames >= 5) {
                    Log.d("VideoToGifConverter", "Breaking extraction: 5 consecutive null frames at $currentUs Us.")
                    break
                }
            }
            currentUs += intervalUs
        }
        
        encoder.finish()
        
        if (addedFrames == 0) {
            Log.e("VideoToGifConverter", "No frames were successfully encoded into the GIF.")
            return false
        }
        
        Log.d("VideoToGifConverter", "Successfully converted video to GIF with $addedFrames frames.")
        return true
    } catch (e: Exception) {
        Log.e("VideoToGifConverter", "Error during conversion: ${e.message}", e)
        return false
    } finally {
        try {
            retriever.release()
        } catch (e: Exception) {}
        try {
            fos?.close()
        } catch (e: Exception) {}
    }
}
