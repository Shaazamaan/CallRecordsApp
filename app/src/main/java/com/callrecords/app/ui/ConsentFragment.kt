package com.callrecords.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.callrecords.app.databinding.FragmentConsentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConsentFragment(private val onConsentConfirmed: () -> Unit) : BottomSheetDialogFragment() {

    private var _binding: FragmentConsentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cbConsent.setOnCheckedChangeListener { _, isChecked ->
            binding.btnConfirm.isEnabled = isChecked
        }

        binding.btnConfirm.setOnClickListener {
            onConsentConfirmed()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ConsentFragment"
    }
}
