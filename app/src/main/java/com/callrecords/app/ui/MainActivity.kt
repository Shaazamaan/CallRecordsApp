package com.callrecords.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.callrecords.app.databinding.ActivityMainBinding
import com.callrecords.app.service.RecordingService
import com.callrecords.app.data.StorageHelper
import com.callrecords.app.R

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isRecording = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.RECORD_AUDIO] == true) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Microphone permission is required", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
        setupListeners()
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    private fun setupListeners() {
        binding.fabRecord.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                showConsentDialog()
            }
        }

        binding.btnLibrary.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        binding.btnFolder.setOnClickListener {
            openFolder()
        }
    }

    private fun showConsentDialog() {
        val dialog = ConsentFragment {
            startRecording()
        }
        dialog.show(supportFragmentManager, ConsentFragment.TAG)
    }

    private fun startRecording() {
        isRecording = true
        updateUi()
        
        val intent = Intent(this, RecordingService::class.java).apply {
            action = RecordingService.ACTION_START
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopRecording() {
        isRecording = false
        updateUi()

        val intent = Intent(this, RecordingService::class.java).apply {
            action = RecordingService.ACTION_STOP
        }
        startService(intent)
    }

    private fun updateUi() {
        if (isRecording) {
            binding.fabRecord.setImageResource(R.drawable.ic_stop)
            binding.tvStatusValue.text = "RECORDING"
            binding.tvStatusValue.setTextColor(ContextCompat.getColor(this, R.color.recording_red))
        } else {
            binding.fabRecord.setImageResource(R.drawable.ic_record)
            binding.tvStatusValue.text = "READY"
            binding.tvStatusValue.setTextColor(ContextCompat.getColor(this, R.color.success))
        }
    }

    private fun openFolder() {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.parse(StorageHelper.getRecordingDirectory().absolutePath)
        intent.setDataAndType(uri, "resource/folder")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Fallback for some file managers
            val fallbackIntent = Intent(Intent.ACTION_GET_CONTENT)
            fallbackIntent.setDataAndType(uri, "*/*")
            startActivity(Intent.createChooser(fallbackIntent, "Open Folder"))
        }
    }
}
