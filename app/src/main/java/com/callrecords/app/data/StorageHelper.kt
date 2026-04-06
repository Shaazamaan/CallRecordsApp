package com.callrecords.app.data

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StorageHelper {
    private const val DIRECTORY_NAME = "CallRecordsApp"

    /**
     * Gets the custom directory for recordings in the public Music folder or Documents.
     * On Android 10+, we use standard public directories for visibility.
     */
    fun getRecordingDirectory(): File {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        val dir = File(root, DIRECTORY_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Generates a unique filename for a new recording.
     * Format: Call_YYYY-MM-DD_HH-mm.m4a
     */
    fun generateFilename(): String {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
        return "Call_$timeStamp.m4a"
    }

    /**
     * Returns the full file path for a new recording.
     */
    fun getNewFilePath(): String {
        return File(getRecordingDirectory(), generateFilename()).absolutePath
    }

    /**
     * Lists all recordings in the directory.
     */
    fun getAllRecordings(): List<File> {
        val dir = getRecordingDirectory()
        return dir.listFiles { file -> file.extension == "m4a" || file.extension == "mp3" }?.toList() ?: emptyList()
    }
}
