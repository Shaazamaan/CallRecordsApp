package com.callrecords.app.core

import android.media.MediaRecorder
import android.os.Build
import android.content.Context
import java.io.File
import java.io.IOException

class RecordingManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var currentFilePath: String? = null

    /**
     * Prepares and starts the recording.
     * Uses MediaRecorder.AudioSource.VOICE_COMMUNICATION for clarity.
     */
    fun startRecording(outputFilePath: String) {
        currentFilePath = outputFilePath
        
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128000)
            setOutputFile(outputFilePath)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Stops and releases the MediaRecorder.
     */
    fun stopRecording(): String? {
        val lastPath = currentFilePath
        try {
            mediaRecorder?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
            // If stop fails (e.g., recorded too short), delete the empty file
            lastPath?.let { File(it).delete() }
        } finally {
            mediaRecorder?.release()
            mediaRecorder = null
            currentFilePath = null
        }
        return lastPath
    }

    fun isRecording(): Boolean = mediaRecorder != null
}
