package com.callrecords.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callrecords.app.data.StorageHelper
import com.callrecords.app.databinding.ActivityLibraryBinding
import com.callrecords.app.databinding.ItemRecordingBinding
import java.io.File

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recordings = StorageHelper.getAllRecordings()
        if (recordings.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvRecordings.layoutManager = LinearLayoutManager(this)
            binding.rvRecordings.adapter = RecordingAdapter(recordings) { file ->
                shareFile(file)
            }
        }
    }

    private fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Recording"))
    }

    inner class RecordingAdapter(
        private val list: List<File>,
        private val onShareClick: (File) -> Unit
    ) : RecyclerView.Adapter<RecordingAdapter.ViewHolder>() {

        inner class ViewHolder(val itemBinding: ItemRecordingBinding) : RecyclerView.ViewHolder(itemBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemBinding = ItemRecordingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val file = list[position]
            holder.itemBinding.tvFileName.text = file.name
            holder.itemBinding.tvDetails.text = "${String.format("%.2f", file.length() / (1024.0 * 1024.0))} MB"
            
            holder.itemBinding.btnShare.setOnClickListener { onShareClick(file) }
            
            holder.itemBinding.root.setOnClickListener {
                // Play logic would go here
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
